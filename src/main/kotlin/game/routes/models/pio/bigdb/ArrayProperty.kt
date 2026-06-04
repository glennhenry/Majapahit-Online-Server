package game.routes.models.pio.bigdb

import kotlinx.serialization.Serializable

@Serializable
data class ArrayProperty(
    val index: Int = 0,
    val value: ValueObject = ValueObject()
)
