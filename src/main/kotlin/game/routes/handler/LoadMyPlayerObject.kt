package game.routes.handler

import game.routes.models.pio.bigdb.LoadMyPlayerObjectOutput
import game.routes.utils.withSuccessHeader
import encore.annotation.source.WeirdBehavior
import encore.context.ServerContext
import encore.serialization.Protobuf
import game.domain.model.TutorialData
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.serialization.ExperimentalSerializationApi

/**
 * LoadMyPlayerObject API 103 is only used to load a `playerObject`
 * which only contain tutorial data.
 */
@OptIn(ExperimentalSerializationApi::class)
suspend fun RoutingContext.loadMyPlayerObject(serverContext: ServerContext) {
    // empty args
    val args = call.receiveChannel().toByteArray()

    @WeirdBehavior(
        "The client doesn't send identifier like playerId or username to load the data." +
                "We have no idea how to identify this API request." +
                "Currently, we send new tutorial data everytime."
    )
//    val data = serverContext.db.loadPlayerData("") ?: return
//    val output = LoadMyPlayerObjectOutput.fromData(data.tutorialData)

    val data = TutorialData()
    val output = LoadMyPlayerObjectOutput.fromData(data)

    val encodedOutput = Protobuf.encode<LoadMyPlayerObjectOutput>(output)

    call.respondBytes(encodedOutput.withSuccessHeader())
}
