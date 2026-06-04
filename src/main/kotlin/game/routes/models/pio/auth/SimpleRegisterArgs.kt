package game.routes.models.pio.auth

import game.routes.models.pio.common.KeyValuePair
import kotlinx.serialization.Serializable

@Serializable
data class SimpleRegisterArgs(
    val gameId: String = "",
    val username: String = "",
    val password: String = "",
    val email: String = "",
    val captchaKey: String = "",
    val captchaValue: String = "",
    val extra: List<KeyValuePair> = emptyList(),
    val partnerId: String = "",
    val playerInsightSegments: List<String> = emptyList(),
    val clientAPI: String = "",
    val clientInfo: List<KeyValuePair> = emptyList(),
)
