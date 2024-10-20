package top.nene.plugins.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import top.nene.plugins.utils.Database.Users

data class LoginRequest(val username:String,val password:String)

fun Routing.login(){
    runCatching {
        
            post("/login") {
                val request = call.receive<LoginRequest>()

                // 查询用户
                val user = transaction {
                    Users.select { Users.username eq request.username }
                        .map { it[Users.passwordHash] }
                        .singleOrNull()
                }

                if (user == null || !BCrypt.checkpw(request.password, user)) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        typeInfo = TODO()
                    )
                } else {
                    //登录成功，鉴权进入/getWxOpenID
                }
            }
        }.onFailure { 
            it.printStackTrace()
    }
}