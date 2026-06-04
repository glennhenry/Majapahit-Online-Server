package game.routes.models.pio.room

import kotlinx.serialization.Serializable

@Serializable
data class CreateRoomOutput(
    val roomId: String = ""
)
