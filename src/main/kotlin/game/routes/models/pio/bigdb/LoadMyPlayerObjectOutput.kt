package game.routes.models.pio.bigdb

import game.routes.utils.BigDBConverter
import kotlinx.serialization.Serializable

@Serializable
data class LoadMyPlayerObjectOutput(
    val playerObject: BigDBObject = BigDBObject()
) {
    companion object {
        inline fun <reified T : Any> fromData(obj: T): LoadMyPlayerObjectOutput {
            return LoadMyPlayerObjectOutput(BigDBConverter.toBigDBObject(obj = obj))
        }
    }
}
