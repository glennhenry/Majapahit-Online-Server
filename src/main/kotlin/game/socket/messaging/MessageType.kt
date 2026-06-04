package game.socket.messaging

/**
 * Types of network message used by MOKV game.
 */
@Suppress("Unused", "SpellCheckingInspection")
object MessageType {
    // custom definition
    const val JOIN = "join"
    const val JOIN_RESULT = "playerio.joinresult"

    // from game (the game doesn't refer to constants, they were hardcoded)
    const val FINISH_LOAD = "finishLoad"
}
