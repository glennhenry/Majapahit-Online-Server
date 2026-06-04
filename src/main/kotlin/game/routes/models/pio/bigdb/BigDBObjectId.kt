package game.routes.models.pio.bigdb

import kotlinx.serialization.Serializable

@Serializable
data class BigDBObjectId(
    val table: String = "",
    val keys: List<String> = emptyList()
)
