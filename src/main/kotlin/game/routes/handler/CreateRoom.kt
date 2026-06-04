package game.routes.handler

import encore.context.ServerContext
import encore.serialization.Protobuf
import game.routes.models.pio.room.CreateRoomArgs
import game.routes.models.pio.room.CreateRoomOutput
import game.routes.utils.withSuccessHeader
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.serialization.ExperimentalSerializationApi

/**
 * CreateRoom (API 21)
 *
 * - Output roomId is expected to be same as the roomId from CreateRoomArgs
 */
@OptIn(ExperimentalSerializationApi::class)
suspend fun RoutingContext.createRoom(serverContext: ServerContext) {
    val createRoomArgs = Protobuf.decode<CreateRoomArgs>(
        call.receiveChannel().toByteArray()
    )

    val output = serverContext.subunits.room.createRoom(createRoomArgs)
    val encodedOutput = Protobuf.encode<CreateRoomOutput>(output)

    call.respondBytes(encodedOutput.withSuccessHeader())
}
