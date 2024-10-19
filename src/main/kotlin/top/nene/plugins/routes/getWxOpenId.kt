package top.nene.plugins.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*
import top.nene.ext.*
import top.nene.plugins.utils.*


fun Routing.getWxOpenId() {
    kotlin.runCatching {
        post("/getwxid") {
            val openId = fetchPost("wxOpenId")
            val meterID = testOpenid(getOpenIdByRegex(openId))
            if (meterID.equals("114514")) {
                call.respond(
                    mapOf(
                        "message" to 404,
                        "status" to "error",
                        "openid" to getOpenIdByRegex(openId),
                        "meterID" to "fail"
                    ).json.toString()
                )
                return@post
            } else {
                call.respond(
                    mapOf(
                        "message" to 200,
                        "status" to "success",
                        "openid" to getOpenIdByRegex(openId),
                        "meterID" to meterID
                    ).json.toString()
                )
            }
        }
    }.onFailure {
        it.printStackTrace()
    }.onSuccess {
    }
}

