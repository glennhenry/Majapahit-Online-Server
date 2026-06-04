package game.routes.hanadler

import game.routes.models.pio.auth.PlayerIORegistrationError
import game.routes.models.pio.auth.PlayerInsightState
import game.routes.models.pio.auth.SimpleRegisterArgs
import game.routes.models.pio.auth.SimpleRegisterOutput
import encore.context.ServerContext
import encore.fancam.Fancam
import encore.serialization.Protobuf
import encore.utils.types.isFail
import encore.utils.types.okOrThrow
import io.ktor.server.request.receiveChannel
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.*
import io.ktor.utils.io.toByteArray
import kotlinx.serialization.ExperimentalSerializationApi

/**
 * SimpleRegister (API 403)
 */
@OptIn(ExperimentalSerializationApi::class)
suspend fun RoutingContext.simpleRegister(serverContext: ServerContext) {
    val simpleRegisterArgs = Protobuf.decode<SimpleRegisterArgs>(
        call.receiveChannel().toByteArray()
    )

    val registrationResult = serverContext.subunits.auth.register(
        simpleRegisterArgs.username,
        simpleRegisterArgs.password,
        simpleRegisterArgs.email
    )

    if (registrationResult.isFail()) {
        Fancam.error { "Registration failed on server..." }
        // [unimplemented] handle failed registration... use PlayerIORegistrationError
        return
    }

    val session = registrationResult.okOrThrow()
    val output = SimpleRegisterOutput(
        token = session.token,
        userId = session.userId,
        showBranding = true,
        gameFSRedirectMap = "1",
        partnerId = "",
        playerInsightState = PlayerInsightState()
    )

    Fancam.info { "Successfully registered user: '${simpleRegisterArgs.username}'" }

    call.respondBytes(Protobuf.encode<SimpleRegisterOutput>(output))
}


@OptIn(ExperimentalSerializationApi::class)
private fun handleFailedRegistration(): ByteArray {
    fun errorFor(field: String): String {
        return "$field error"
    }

    val errorOutput = PlayerIORegistrationError(
        usernameError = errorFor("Username"),
        passwordError = errorFor("Password"),
        emailError = errorFor("Email"),
        captchaError = ""
    )

    return Protobuf.encode(errorOutput)
}
