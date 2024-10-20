package top.nene.plugins.utils.Database.Jedisutils

import redis.clients.jedis.Jedis

// 创建 Redis 客户端实例
val jedis = Jedis("localhost", 6379)

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
