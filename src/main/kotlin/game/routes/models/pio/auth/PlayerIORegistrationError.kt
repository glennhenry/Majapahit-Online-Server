package game.routes.models.pio.auth

import kotlinx.serialization.Serializable

@Serializable
data class PlayerIORegistrationError(
    val usernameError: String = "",
    val passwordError: String = "",
    val emailError: String = "",
    val captchaError: String = ""
)
