package game.routes.models.pio.error

import game.routes.models.pio.common.KeyValuePair
import kotlinx.serialization.Serializable

@Serializable
data class WriteErrorArgs(
    val source: String = "",
    val error: String = "",
    val details: String = "",
    val stacktrace: String = "",
    val extraData: List<KeyValuePair> = listOf()
)
