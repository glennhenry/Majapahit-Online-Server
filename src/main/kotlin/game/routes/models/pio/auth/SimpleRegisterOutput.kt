package game.routes.models.pio.auth

import kotlinx.serialization.Serializable

@Serializable
data class SimpleRegisterOutput(
    val token: String = "",
    val userId: String = "",
    val showBranding: Boolean = false,
    val gameFSRedirectMap: String = "",
    val partnerId: String = "",
    val playerInsightState: PlayerInsightState = PlayerInsightState()
)
