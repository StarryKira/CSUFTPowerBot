package top.nene

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import top.nene.plugins.*
import top.nene.plugins.routes.configureRouting
import top.nene.plugins.routes.getWxOpenId


private val logger: Logger = LogManager.getLogger("LoginRoute")

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
}

