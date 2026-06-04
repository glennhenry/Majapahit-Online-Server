package game.routes.models.pio.auth

import kotlinx.serialization.Serializable

@Serializable
data class SimpleConnectError(
    val errorCode: Int = 0,
    val message: String = ""
)
