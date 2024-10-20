package top.nene.plugins.utils.Database.PostgreSQLutils

import org.jetbrains.exposed.sql.Database

fun connectToDatabase() {
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/mydatabase",//数据库地址
        driver = "org.postgresql.Driver",
        user = "myuser",
        password = "mypassword"
        //填写自己的用户名和密码
    )
}
