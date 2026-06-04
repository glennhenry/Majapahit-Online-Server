package game.routes.models.pio.common

import kotlinx.serialization.Serializable

@Serializable
data class ServerEndpoint(
    val address: String = "",
    val port: Int = 0
)
