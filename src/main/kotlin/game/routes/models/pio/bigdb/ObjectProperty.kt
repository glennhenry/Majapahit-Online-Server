package game.routes.models.pio.bigdb

import kotlinx.serialization.Serializable

@Serializable
data class ObjectProperty(
    val name: String = "",
    val value: ValueObject = ValueObject()
)
