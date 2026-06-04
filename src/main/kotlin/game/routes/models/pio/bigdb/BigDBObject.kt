package game.routes.models.pio.bigdb

import kotlinx.serialization.Serializable

@Serializable
data class BigDBObject(
    val key: String = "",
    val version: String = "",
    val properties: List<ObjectProperty> = emptyList(),
    val creator: UInt = 0u,
)
