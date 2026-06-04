package game.socket.handler

import encore.context.ServerContext
import encore.fancam.Fancam
import encore.network.handler.FanchantHandler
import encore.network.handler.HandlerContext
import encore.network.handler.playerId
import encore.utils.types.okOrNull
import game.socket.messaging.MessageType
import game.socket.messaging.PIOSerializer
import game.socket.messaging.PioFanchant
import kotlin.reflect.KClass

/**
 * Handle `join` message by:
 *
 * 1. Sending `playerio.joinresult`
 * 2. Sending `gr` message
 */
class JoinHandler(private val serverContext: ServerContext) : FanchantHandler<PioFanchant> {
    override val fanchantType: String = MessageType.JOIN
    override val expectedFanchantClass: KClass<PioFanchant> = PioFanchant::class

    override suspend fun handle(ctx: HandlerContext<PioFanchant>): Unit = with(ctx) {
        val joinKey = fanchant.getString("join") ?: return@with
        val playerName = fanchant.getString("username") ?: ""

        // player is identified at this point
        serverContext.subunits.account.getPlayerIdByUsername(playerName)
            .okOrNull()
            .let { playerId ->
                if (playerId == null) {
                    Fancam.warn { "PlayerId not found for $playerName (this should work)" }
                } else {
                    // acknowledge the connection
                    connection.updateIdentity(playerId, playerName)

                    // call onIdentified hook
                    serverContext.playerLifecycleHandler.onIdentified(serverContext, connection)

                    // mark online
                    serverContext.subunits.presence.markOnline(playerId)

                    // create context
                    serverContext.contextRegistry.createContext(playerId, connection)
                }
            }

        Fancam.info(tag = "join") {
            "Received joinKey '$joinKey', from username '$playerName', playerId is '${playerId()}'"
        }

        // join result success (with boolean true)
        // for join result fail must use error string and error code
        val response = listOf(MessageType.JOIN_RESULT, true)
        connection.write(PIOSerializer.serialize(response))

        // next thing to do, investigate loadData
        connection.write(PIOSerializer.serialize(listOf("loadData")))
    }
}
