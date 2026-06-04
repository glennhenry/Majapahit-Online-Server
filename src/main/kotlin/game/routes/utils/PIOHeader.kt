package game.routes.utils

/**
 * Adds a success PlayerIO header to the byte array.
 *
 * As per PlayerIO API response convention, the header `0x00 0x01` is needed
 * for a succesful response.
 *
 * @receiver The original unframed [ByteArray] representing a protocol buffer message.
 * @return A new [ByteArray] with `0x00` and `0x01` prepended.
 */
fun ByteArray.withSuccessHeader(): ByteArray {
    return byteArrayOf(0, 1) + this
}

/**
 * Adds an error PlayerIO header to the byte array.
 *
 * As per PlayerIO API response convention, the header `0x00 0x00` is needed
 * for an error response.
 *
 * @receiver The original unframed [ByteArray] representing a protocol buffer message.
 * @return A new [ByteArray] with `0x00` and `0x00` prepended.
 */
fun ByteArray.withErrorHeader(): ByteArray {
    return byteArrayOf(0, 0) + this
}
