package game.routes.handler

import encore.context.ServerContext
import encore.fancam.Fancam
import encore.serialization.Protobuf
import game.routes.models.pio.room.ListRoomsArgs
import game.routes.models.pio.room.ListRoomsOutput
import game.routes.utils.withSuccessHeader
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import kotlinx.serialization.ExperimentalSerializationApi

/**
 * ListRooms (API 30)
 *
 * To list online players within a room.
 *
 * - First request to ListRooms is during game load, made by MOKV.as.
 *   The request is called every second (online players polling)
 * - Some other class that made the request: BattleArenaUI.as and WaitingRoomUI.as
 */
@OptIn(ExperimentalSerializationApi::class)
suspend fun RoutingContext.listRooms(
    serverContext: ServerContext,
    firstListRoomLog: Boolean, turnOffLog: () -> Unit
) {
    val listRoomsArgs = Protobuf.decode<ListRoomsArgs>(
        call.receiveChannel().toByteArray()
    )

    val output = serverContext.subunits.room.listRooms(listRoomsArgs)
    val encodedOutput = Protobuf.encode<ListRoomsOutput>(output)

    if (firstListRoomLog) {
        Fancam.trace { "Repeated log for API 30 will be disabled from now on." }
        turnOffLog()
    }

    call.respondBytes(encodedOutput.withSuccessHeader())
}
