package game.routes

import encore.context.ServerContext
import encore.fancam.Fancam
import encore.route.RouteHandler
import encore.route.guard.NoAuthGuard
import encore.route.handle
import game.routes.hanadler.simpleRegister
import game.routes.handler.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class ApiRoutes(private val serverContext: ServerContext) : RouteHandler {
    private var firstListRoomLog = true

    override fun Route.install() {
        post("/api/{path}") {
            handle(call, NoAuthGuard) {
                val path = call.parameters["path"]
                    ?: return@handle call.respond(HttpStatusCode.BadRequest)

                when (path) {
                    "21" -> createRoom(serverContext)
                    "24" -> joinRoom(serverContext)
                    "30" -> listRooms(serverContext, firstListRoomLog, { firstListRoomLog = false })
                    "85" -> stubHandler()
                    "97" -> stubHandler()
                    "103" -> loadMyPlayerObject(serverContext)
                    "403" -> simpleRegister(serverContext)
                    else -> {
                        Fancam.error(tag = "API_ERR") { "Unimplemented API route: /api/$path" }
                        call.respond(HttpStatusCode.NotFound, "Unimplemented API: /api/$path")
                    }
                }
            }
        }

        /**
         * The game independently request to /register, providing email and username
         * after getting a successful registration from API 403 of SimpleRegister.
         *
         * Ignore it since we have already registered the account in the API.
         */
        get("/register") {
            handle(call, NoAuthGuard) {
                stubHandler()
            }
        }
    }
}
