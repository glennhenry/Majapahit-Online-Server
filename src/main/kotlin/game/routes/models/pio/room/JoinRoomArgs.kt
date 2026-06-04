package game.routes.models.pio.room

import game.routes.models.pio.common.KeyValuePair
import kotlinx.serialization.Serializable

@Serializable
data class JoinRoomArgs(
    val roomId: String = "",
    val joinData: List<KeyValuePair> = emptyList(),
    val isDevRoom: Boolean = false,
)
