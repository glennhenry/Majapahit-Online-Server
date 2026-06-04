package game.routes.models.pio.room

import kotlinx.serialization.Serializable

@Serializable
data class ListRoomsOutput(
    val rooms: List<RoomInfo> = emptyList()
)
