package game.routes.hanadler

import game.routes.models.pio.auth.PlayerIORegistrationError
import game.routes.models.pio.auth.PlayerInsightState
import game.routes.models.pio.auth.SimpleRegisterArgs
import game.routes.models.pio.auth.SimpleRegisterOutput
import encore.context.ServerContext
import encore.fancam.Fancam
import encore.security.Screening
import encore.serialization.Protobuf
import encore.utils.types.isFail
import encore.utils.types.okOrThrow
import game.routes.utils.withErrorHeader
import game.routes.utils.withSuccessHeader
import io.ktor.http.content.ByteArrayContent
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

    var failedRegisterResponse: ByteArray? = null
    Screening("Verify register")
        .check(
            description = "Email non-blank",
            predicate = { simpleRegisterArgs.email.isNotBlank() },
            onFail = { failedRegisterResponse = handleFailedRegistration(3) }
        )
        .check(
            description = "Email contains '@'",
            predicate = { simpleRegisterArgs.email.contains("@") },
            onFail = { failedRegisterResponse = handleFailedRegistration(3) }
        )
        .checkSuspend(
            description = "Email is unique",
            predicate = { serverContext.subunits.auth.isEmailAvailable(simpleRegisterArgs.email).okOrThrow() },
            onFail = { failedRegisterResponse = handleFailedRegistration(3) }
        )
        .check(
            description = "Username non-blank",
            predicate = { simpleRegisterArgs.username.isNotBlank() },
            onFail = { failedRegisterResponse = handleFailedRegistration(1) }
        )
        .check(
            description = "Username does not contain bad words",
            predicate = { !simpleRegisterArgs.username.contains("dick") },
            onFail = { failedRegisterResponse = handleFailedRegistration(1) }
        )
        .checkSuspend(
            description = "Username is unique",
            predicate = { serverContext.subunits.auth.isUsernameAvailable(simpleRegisterArgs.username).okOrThrow() },
            onFail = { failedRegisterResponse = handleFailedRegistration(1) }
        )
        .check(
            description = "Password length 5 length at minimum",
            predicate = { simpleRegisterArgs.password.length > 5 },
            onFail = { failedRegisterResponse = handleFailedRegistration(2) }
        )

    // which mean one of the check failed
    if (failedRegisterResponse != null) {
        call.respondBytes(failedRegisterResponse)
        return
    }

    val registrationResult = serverContext.subunits.auth.register(
        simpleRegisterArgs.username,
        simpleRegisterArgs.password,
        simpleRegisterArgs.email
    )

    if (registrationResult.isFail()) {
        Fancam.error { "Registration failed on server..." }
        return
    }

    val session = registrationResult.okOrThrow()
    val output = SimpleRegisterOutput(
        token = session.token,
        userId = session.userId,
        showBranding = true,
        gameFSRedirectMap = "",
        partnerId = "",
        playerInsightState = PlayerInsightState()
    )

    Fancam.info { "Successfully registered user: '${simpleRegisterArgs.username}'" }

    call.respondBytes(Protobuf.encode<SimpleRegisterOutput>(output).withSuccessHeader())
}

@OptIn(ExperimentalSerializationApi::class)
private fun handleFailedRegistration(whatError: Int): ByteArray {
    val errorOutput = when (whatError) {
        1 -> {
            PlayerIORegistrationError(
                usernameError = "Username error",
                passwordError = "",
                emailError = "",
                captchaError = ""
            )
        }

        2 -> {
            PlayerIORegistrationError(
                usernameError = "",
                passwordError = "Password error",
                emailError = "",
                captchaError = ""
            )
        }

        3 -> {
            PlayerIORegistrationError(
                usernameError = "",
                passwordError = "",
                emailError = "Email error",
                captchaError = ""
            )
        }

        4 -> {
            PlayerIORegistrationError(
                usernameError = "",
                passwordError = "",
                emailError = "",
                captchaError = "Captcha error"
            )
        }

        else -> {
            PlayerIORegistrationError(
                usernameError = "",
                passwordError = "",
                emailError = "",
                captchaError = ""
            )
        }
    }

    return Protobuf.encode(errorOutput).withErrorHeader()
}
