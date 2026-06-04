package game.routes.handler

import encore.context.ServerContext
import encore.serialization.Protobuf
import game.routes.models.pio.error.PlayerIOError
import game.routes.models.pio.room.JoinRoomArgs
import game.routes.models.pio.room.JoinRoomOutput
import game.routes.utils.withSuccessHeader
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.serialization.ExperimentalSerializationApi

/**
 * JoinRoom (API 30)
 */
@OptIn(ExperimentalSerializationApi::class)
suspend fun RoutingContext.joinRoom(serverContext: ServerContext) {
    val joinRoomArgs = Protobuf.decode<JoinRoomArgs>(
        call.receiveChannel().toByteArray()
    )

    val output = serverContext.subunits.room.joinRoom(joinRoomArgs)
    val encodedOutput = if (output == null) {
        Protobuf.encode<PlayerIOError>(
            PlayerIOError(errorCode = 17, "Unknown Room ID"),
        )
    } else {
        Protobuf.encode<JoinRoomOutput>(output)
    }

    call.respondBytes(encodedOutput.withSuccessHeader())
}
