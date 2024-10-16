package top.nene.plugins.useradmin

import io.ktor.http.*
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.OkHttpClient
import top.nene.plugins.Cookie
import top.nene.ext.*

fun refreshPowerBill(meterId:String):String{
    runCatching {
        val url = "http://wsjdf.csuft.edu.cn/wx/api/main/wxpay/meterread"
        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("meterId", meterId)
            .build();
        val headers = Headers.Builder()
            .add("Cookie","JSESSIONID=$Cookie")
            .build();
        val request = okhttp3.Request.Builder()
            .url(url)
            .headers(headers)
            .post(formBody)
            .build()
        val response = client.newCall(request).execute()
        if (response.code == 200) {
            val jsonText = response.body.toString()
            val json = jsonText.asJsonObject
            val data = json["data"]
            println("successfully get money ${data.toString()}")
            return data.toString()
        }
    }
    return "0721"
}