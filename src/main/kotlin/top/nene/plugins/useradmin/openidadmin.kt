package top.nene.plugins.useradmin


import kotlinx.serialization.json.JsonObject
import okhttp3.*
import org.postgresql.core.Tuple
import top.nene.ext.asJsonArray
import top.nene.ext.asJsonObject
import top.nene.ext.asString
import top.nene.plugins.Cookie


fun getOpenIdByRegex(wxLink:String):String{
    val regex = "(?<=id=)[^&]+".toRegex()
    val result = regex.find(wxLink)
    return result?.value ?: "114514"
}

fun testOpenid(wxLink: String): String {
    runCatching {
        val url = "http://wsjdf.csuft.edu.cn/wx/app/api/user/searchBindHouseListForMoney"
        val client = OkHttpClient()
        val formBody = FormBody.Builder()
            .add("wxId", wxLink)
            .build();
        val headers = Headers.Builder()
            .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 NetType/WIFI MicroMessenger/7.0.20.1781(0x6700143B) WindowsWechat(0x63090b0f) XWEB/11275 Flue")
            .add("Cookie","JSESSIONID=$Cookie")
            .build();
        val request = Request.Builder()
            .url(url)
            .headers(headers)
            .post(formBody)
            .build()
        val response = client.newCall(request).execute()
        if (response.code == 200) {
            val jsonText = String(response.body!!.bytes())
            val json = jsonText.asJsonObject
            val data = json["data"]!!.asJsonArray
            if (!data.isEmpty()) {
                return data.first()
                    .asJsonObject["meter"]
                    .asJsonArray.first()
                    .asJsonObject["meter_id"]
                    .asString
            } else {
                println(jsonText)
            }
        }
    }.onFailure {
        it.printStackTrace()
    }.onSuccess {
        println("successfully get meterID")
    }
    return "114514"
}