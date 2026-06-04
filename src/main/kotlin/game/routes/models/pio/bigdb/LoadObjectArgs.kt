package game.routes.models.pio.bigdb

import kotlinx.serialization.Serializable

@Serializable
data class LoadObjectsArgs(
    val objectIds: List<BigDBObjectId> = listOf()
)
