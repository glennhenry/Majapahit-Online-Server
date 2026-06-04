package game.routes.models.pio.auth

import game.routes.models.pio.common.KeyValuePair
import kotlinx.serialization.Serializable

@Serializable
data class SimpleConnectArgs(
    val gameId: String = "",
    val usernameOrEmail: String = "",
    val password: String = "",
    val playerInsightSegments: List<String> = emptyList(),
    val clientAPI: String = "",
    val clientInfo: List<KeyValuePair> = emptyList(),
)
