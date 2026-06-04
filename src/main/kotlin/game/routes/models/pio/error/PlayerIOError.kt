package game.routes.models.pio.error

import kotlinx.serialization.Serializable

/**
 * A generic PlayerIO error, used on many API's error response.
 *
 * @property errorCode Error code number, see PIO client's definition.
 */
@Serializable
data class PlayerIOError(
    val errorCode: Int = 0,
    val message: String = ""
)
