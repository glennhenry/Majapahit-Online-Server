package game.routes.models.pio.auth

import kotlinx.serialization.Serializable

@Serializable
data class SimpleRegisterError(
    val errorCode: Int = 0,
    val message: String = "",
    val usernameError: String = "",
    val passwordError: String = "",
    val emailError: String = "",
    val captchaError: String = ""
)
