package top.nene.plugins.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*
import top.nene.ext.fetchPost
import top.nene.plugins.utils.refreshPowerBill

fun Routing.refreshPowerBill(){
    runCatching {
        post("/refreshPowerBill"){
            var meterID:String = fetchPost("meterID")
            call.respond(
                mapOf(
                    "money" to refreshPowerBill(meterID)
                )
            )
        }
    }
}
