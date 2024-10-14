package top.nene.plugins.useradmin

import io.ktor.http.*
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.OkHttpClient
import top.nene.plugins.Cookie

fun refreshPowerBill(meterId:String):Double{
    runCatching {
        val url = "http://wsjdf.csuft.edu.cn/wx/api/main/wxpay/meterread"
        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("meterId", meterId)
            .build();
        val headers = Headers.Builder()
            .add("Cookie","JSESSIONID=$Cookie")
        return 0.721
    }
    return 0.0
}