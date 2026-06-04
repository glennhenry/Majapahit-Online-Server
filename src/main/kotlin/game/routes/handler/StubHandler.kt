package game.routes.handler

import encore.fancam.Fancam
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.*

/**
 * Stub handler used as placeholder routes, typically used as "TO-DO",
 * or routes that don't need a response.
 */
suspend fun RoutingContext.stubHandler() {
    val request = call.receiveText()
    val printRequest = request.ifBlank { "[Empty payload]" }

    Fancam.debug(tag = "Stub_API") {
        "Received stub API to ${call.request.httpMethod} ${call.request.uri}: $printRequest"
    }

    call.respondText("Unimplemented", status = HttpStatusCode.OK)
}
