package top.nene.plugins.utils.Database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50)
    val passwordHash = varchar("password_hash", 64)
    val wxOpenID = varchar("openid",128)
    val roomID = varchar("roomID",20)
    val meterID = varchar("meter_id",128)
    override val primaryKey = PrimaryKey(id)
}

