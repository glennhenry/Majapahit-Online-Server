package api.routes

import io.ktor.http.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.fileRoutes() {
    get("/") {
        call.respondFile(File("static/index.html"))
    }
    staticFiles("site", File("static/site"))
    staticFiles("game", File("static/game"))

    get("/docs") {
        val docsIndex = File("docs/index.html")
        if (docsIndex.exists()) {
            call.respondFile(docsIndex)
        } else {
            call.respond(HttpStatusCode.NotFound, "Only available in production; Please start the docs using vite server or build the server first.")
        }
    }

    staticFiles("docs", File("docs"))
}
