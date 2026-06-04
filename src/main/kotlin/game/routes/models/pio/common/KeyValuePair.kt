package game.routes.models.pio.common

import kotlinx.serialization.Serializable

@Serializable
data class KeyValuePair(
    val key: String = "",
    val value: String = "",
)
