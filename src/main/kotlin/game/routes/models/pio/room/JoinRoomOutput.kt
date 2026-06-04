package game.routes.models.pio.room

import game.routes.models.pio.common.ServerEndpoint
import kotlinx.serialization.Serializable

@Serializable
data class JoinRoomOutput(
    val joinKey: String = "",
    val endpoints: List<ServerEndpoint> = emptyList()
)
