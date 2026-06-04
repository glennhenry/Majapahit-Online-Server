package game.socket.messaging

import encore.fancam.Fancam
import encore.network.fanchant.guide.DecodeResult
import encore.network.fanchant.guide.FanchantGuide
import encore.serialization.JSON
import encore.serialization.parseJsonToMap
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class PioFanchantGuide : FanchantGuide<List<Any>> {
    override fun verify(data: ByteArray): Boolean {
        val first = data[0].toInt() and 0xFF
        val pattern = Pattern.fromByte(first)
        if (pattern == Pattern.DOES_NOT_EXIST) return false

        when (pattern) {
            Pattern.STRING_PATTERN,
            Pattern.BYTE_ARRAY_PATTERN,
            Pattern.UNSIGNED_INT_PATTERN,
            Pattern.INT_PATTERN -> {
                // header requires at least 1 (pattern) + 4 bytes
                if (data.size < 5) return false
            }

            Pattern.DOUBLE_PATTERN -> if (data.size < 9) return false
            Pattern.FLOAT_PATTERN -> if (data.size < 5) return false

            else -> {
                // short patterns are fine with minimal data
            }
        }

        return true
    }

    override fun tryDecode(data: ByteArray): DecodeResult<List<Any>> {
        var state = "init"
        var pattern = Pattern.DOES_NOT_EXIST
        val buffer = ByteArrayOutputStream()
        var partLength = 0
        var length = -1
        val message = mutableListOf<Any>()

        fun onValue(value: Any?) {
            if (length == -1 && value is Int) {
                length = value
            } else {
                message.add(value ?: "null")
            }
        }

        fun ByteArray.padStart(size: Int): ByteArray {
            return ByteArray(size - this.size) { 0 } + this
        }

        try {
            for (byte in data) {
                when (state) {
                    "init" -> {
                        pattern = Pattern.fromByte(byte.toInt() and 0xFF)
                        val part = byte.toInt() and 0x3F

                        when (pattern) {
                            Pattern.STRING_SHORT_PATTERN,
                            Pattern.BYTE_ARRAY_SHORT_PATTERN -> {
                                partLength = part
                                state = if (partLength > 0) "data" else {
                                    val value = if (pattern == Pattern.STRING_SHORT_PATTERN) "" else ByteArray(0)
                                    onValue(value)
                                    "init"
                                }
                            }

                            Pattern.STRING_PATTERN,
                            Pattern.BYTE_ARRAY_PATTERN,
                            Pattern.UNSIGNED_INT_PATTERN,
                            Pattern.INT_PATTERN -> {
                                partLength = 4
                                state = "header"
                            }

                            Pattern.UNSIGNED_INT_SHORT_PATTERN -> {
                                onValue(part)
                            }

                            Pattern.UNSIGNED_LONG_SHORT_PATTERN,
                            Pattern.LONG_SHORT_PATTERN -> {
                                partLength = 1
                                state = "data"
                            }

                            Pattern.UNSIGNED_LONG_PATTERN,
                            Pattern.LONG_PATTERN -> {
                                partLength = 6
                                state = "data"
                            }

                            Pattern.DOUBLE_PATTERN -> {
                                partLength = 8
                                state = "data"
                            }

                            Pattern.FLOAT_PATTERN -> {
                                partLength = 4
                                state = "data"
                            }

                            Pattern.BOOLEAN_TRUE_PATTERN -> onValue(true)
                            Pattern.BOOLEAN_FALSE_PATTERN -> onValue(false)
                            else -> {} // unsupported
                        }
                    }

                    "header" -> {
                        buffer.write(byte.toInt())
                        if (buffer.size() == partLength) {
                            val padded = buffer.toByteArray().padStart(4)
                            partLength = ByteBuffer.wrap(padded).order(ByteOrder.LITTLE_ENDIAN).int

                            if (partLength !in 0..10_000_000) {
                                throw IllegalArgumentException("Invalid partLength = $partLength")
                            }

                            buffer.reset()
                            state = "data"
                        }
                    }

                    "data" -> {
                        buffer.write(byte.toInt())
                        if (buffer.size() == partLength) {
                            val bytes = buffer.toByteArray()
                            val padded = { b: ByteArray, size: Int -> b.padStart(size) }

                            val value = try {
                                when (pattern) {
                                    Pattern.STRING_SHORT_PATTERN,
                                    Pattern.STRING_PATTERN -> bytes.toString(Charsets.UTF_8)

                                    Pattern.UNSIGNED_INT_PATTERN,
                                    Pattern.INT_PATTERN -> ByteBuffer.wrap(padded(bytes, 4))
                                        .order(ByteOrder.BIG_ENDIAN).int

                                    Pattern.UNSIGNED_LONG_PATTERN,
                                    Pattern.UNSIGNED_LONG_SHORT_PATTERN,
                                    Pattern.LONG_PATTERN,
                                    Pattern.LONG_SHORT_PATTERN -> ByteBuffer.wrap(padded(bytes, 8))
                                        .order(ByteOrder.BIG_ENDIAN).long

                                    Pattern.DOUBLE_PATTERN -> ByteBuffer.wrap(padded(bytes, 8))
                                        .order(ByteOrder.BIG_ENDIAN).double

                                    Pattern.FLOAT_PATTERN -> ByteBuffer.wrap(padded(bytes, 4))
                                        .order(ByteOrder.BIG_ENDIAN).float

                                    Pattern.BYTE_ARRAY_SHORT_PATTERN,
                                    Pattern.BYTE_ARRAY_PATTERN -> bytes

                                    else -> null
                                }
                            } catch (e: Exception) {
                                Fancam.error(e) { "Error deserializing pattern $pattern" }
                                null
                            }

                            onValue(value)
                            buffer.reset()
                            state = "init"
                        }
                    }
                }
            }

            return DecodeResult.Success(message)
        } catch (_: Exception) {
        }

        val offset = data.indexOfFirst { it == '{'.code.toByte() }

        return if (offset != -1) {
            try {
                val jsonBytes = data.copyOfRange(offset, data.size)
                val json = jsonBytes.toString(Charsets.UTF_8)
                val parsed = parseJsonToMap(JSON.json, json)
                val type = message.firstOrNull() as? String

                if (type != null) {
                    val isAlreadyWrapped = parsed.size == 1 && parsed.containsKey(type)

                    val final = if (isAlreadyWrapped) {
                        listOf(type, parsed[type]!!)
                    } else {
                        listOf(type, parsed)
                    }
                    DecodeResult.Success(final)
                } else {
                    Fancam.error { "Cannot determine message type from partial data" }
                    DecodeResult.Failure(reason = "Data is likely incomplete")
                }
            } catch (e: Exception) {
                Fancam.error(e) { "JSON fallback deserialization failed" }
                DecodeResult.Failure(reason = "JSON fallback deserialization failed")
            }
        } else {
            DecodeResult.Failure(reason = "JSON opening bracket not seen")
        }
    }

    override fun materialize(decoded: List<Any>): PioFanchant {
        val map = buildMap {
            val start = if (decoded.size % 2 == 1) {
                1
            } else {
                0
            }

            val end = decoded.size
            for (i in start until end step 2) {
                val key = decoded.getOrNull(i) as? String ?: continue
                val value = decoded.getOrNull(i + 1)
                put(key, value)
            }
        }

        // For PioFanchant, the routing type is usually the first key
        return PioFanchant(map, decoded.first().toString())
    }
}
