package game.routes.models.pio.error

import kotlinx.serialization.Serializable

@Serializable
data class WriteErrorError(
    val errorCode: Int = 0,
    val message: String = "",
)
