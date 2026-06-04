package game.routes.models.pio.bigdb

import game.routes.utils.BigDBConverter
import kotlinx.serialization.Serializable

@Serializable
data class LoadObjectsOutput(
    val objects: List<BigDBObject> = emptyList()
) {
    companion object {
        inline fun <reified T : Any> fromData(vararg obj: T): LoadObjectsOutput {
            return LoadObjectsOutput(
                objects = obj.map { BigDBConverter.toBigDBObject(obj = it) }
            )
        }
    }
}
