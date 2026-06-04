package game.routes.models.pio.room

import game.routes.models.pio.common.KeyValuePair
import kotlinx.serialization.Serializable

@Serializable
data class CreateRoomArgs(
    val roomId: String = "",
    val roomType: String = "",
    val visible: Boolean = false,
    val roomData: List<KeyValuePair> = emptyList(),
    val isDevRoom: Boolean = false,
)
