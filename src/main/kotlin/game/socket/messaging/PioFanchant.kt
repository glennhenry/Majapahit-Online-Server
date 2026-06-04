package game.socket.messaging

import encore.network.fanchant.Fanchant

/**
 * High-level representation of PlayerIO socket message.
 *
 * By convention, the raw (deserialized) PlayerIO message
 * is a `List<Any>` type which is a flat key-value pair list
 * (i.e., `["key1", "value1", "key2", "value2"]`).
 *
 * Note: This doesn't support strong typing, and is just a wrapper
 * over the raw decoded data.
 *
 * @param typeInit The type of PIOMessage, which is the first key in the flat list.
 */
class PioFanchant(val data: Map<String, Any?>, typeInit: String) : Fanchant {
    override val type: String = typeInit

    override fun toString(): String {
        return data.entries.joinToString(
            prefix = "PIOMessage({",
            postfix = "})"
        ) { (k, v) ->
            if (v == "") "$k=\"\"" else "$k=$v"
        }
    }

    // helper functions
    fun contains(key: String): Boolean = data.containsKey(key)
    fun getString(key: String): String? = data[key] as? String
    fun getInt(key: String): Int? = (data[key] as? Number)?.toInt()
    fun getDouble(key: String): Double? = (data[key] as? Number)?.toDouble()
    fun getBoolean(key: String): Boolean? = data[key] as? Boolean
    fun getBytes(key: String): ByteArray? = data[key] as? ByteArray
}
