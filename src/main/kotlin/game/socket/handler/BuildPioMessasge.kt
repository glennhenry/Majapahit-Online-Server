package game.socket.handler

import encore.time.TimeCenter

/**
 * To build PlayerIO message to send to client.
 *
 * 1. Adds an `r` (response) type.
 * 2. Add [saveId].
 * 3. Add time in millisecond (using [TimeCenter]).
 * 4. Add the payloads.
 *
 * @param saveId The corresponding save message ID sent by client.
 * @param payloads Variable number of arguments to be included in the payload.
 */
fun buildPIOMessage(saveId: String?, vararg payloads: Any): List<Any> {
    return buildList {
        add("r")
        add(saveId ?: "m")
        // may want to use toDouble() if time don't work on client (due to type conversion error)
        add(TimeCenter.now())
        addAll(payloads)
    }
}
