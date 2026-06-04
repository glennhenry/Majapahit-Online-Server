package game.routes.models.pio.room

import game.routes.models.pio.common.KeyValuePair
import kotlinx.serialization.Serializable

@Serializable
data class RoomInfo(
    val id: String = "",
    val roomType: String = "",
    val onlineUsers: Int = 0,
    val roomData: List<KeyValuePair> = emptyList()
)
