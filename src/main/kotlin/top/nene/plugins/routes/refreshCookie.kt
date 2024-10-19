package top.nene.plugins.routes

import io.ktor.server.auth.*
import io.ktor.server.routing.*
import top.nene.ext.fetchPost
import top.nene.ext.json
import top.nene.plugins.Cookie
import io.ktor.server.response.*


fun Routing.refreshCookie() {
    runCatching {
            post("/getcookie") {
                Cookie = fetchPost("cookie")
                call.respond(
                    mapOf(
                        "message" to 200,
                        "status" to "success",
                        "cookie" to Cookie
                    ).json
                )
        }
    }.onFailure {
        it.printStackTrace()
    }.onSuccess {
        println("Cookie has been refreshed $Cookie.")
    }
}