package top.nene.ext

import io.ktor.client.engine.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import io.ktor.server.routing.route
import kotlinx.serialization.json.JsonElement
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import io.ktor.utils.io.*

private val keyIsJson = AttributeKey<Boolean>("isJson")
private val keyJsonObject = AttributeKey<JsonObject>("paramsJson")
private val keyJsonArray = AttributeKey<JsonArray>("paramsJsonArray")
private val keyJsonElement = AttributeKey<JsonElement>("paramsJsonElement")
private val keyParts = AttributeKey<Parameters>("paramsParts")

suspend fun ApplicationCall.fetch(key: String): String {
    val isPost = request.httpMethod == HttpMethod.Post
    return if (isPost) {
        fetchPost(key)
    } else {
        fetchGet(key)
    }
}

suspend fun ApplicationCall.fetchOrNull(key: String): String? {
    val isPost = request.httpMethod == HttpMethod.Post
    return if (isPost) {
        fetchPostOrNull(key)
    } else {
        fetchGetOrNull(key)
    }
}

suspend fun ApplicationCall.fetchOrThrow(key: String): String {
    val isPost = request.httpMethod == HttpMethod.Post
    return if (isPost) {
        fetchPostOrThrow(key)
    } else {
        fetchGetOrThrow(key)
    }
}

fun ApplicationCall.fetchGet(key: String): String {
    return parameters[key]!!
}

fun ApplicationCall.fetchGetOrNull(key: String): String? {
    return parameters[key]
}

fun ApplicationCall.fetchGetOrThrow(key: String): String {
    return parameters[key] ?: throw RuntimeException(key)
}

suspend fun ApplicationCall.fetchPost(key: String): String {
    return fetchPostOrNull(key)!!
}

suspend fun ApplicationCall.fetchPostOrThrow(key: String): String {
    return fetchPostOrNull(key) ?: throw RuntimeException(key)
}

fun ApplicationCall.isJsonData(): Boolean {
    return ContentType.Application.Json == request.contentType() || ContentType.Application.ProblemJson == request.contentType()
}

suspend fun ApplicationCall.fetchPostOrNull(key: String): String? {
    if (attributes.contains(keyJsonObject)) {
        return attributes[keyJsonObject][key].asStringOrNull
    }
    if (attributes.contains(keyParts)) {
        return attributes[keyParts][key]
    }
    return kotlin.runCatching {
        if (isJsonData()) {
            Json.parseToJsonElement(receiveText()).jsonObject.also {
                attributes.put(keyJsonObject, it)
                attributes.put(keyIsJson, true)
            }[key].asStringOrNull
        } else if (
            ContentType.Application.FormUrlEncoded == request.contentType()
        ) {
            receiveParameters().also {
                attributes.put(keyParts, it)
            }[key]
        } else {
            receiveTextAsUnknown(key)
        }
    }.getOrElse {
        throw IllegalArgumentException("JSON数据格式不合法")
    }
}

private suspend fun ApplicationCall.receiveTextAsUnknown(key: String): String? {
    return receiveText().let { text ->
        if (text.startsWith("{") && text.endsWith("}")) {
            Json.parseToJsonElement(text).jsonObject.also {
                attributes.put(keyJsonObject, it)
                attributes.put(keyIsJson, true)
            }[key].asStringOrNull
        } else {
            text.parseUrlEncodedParameters().also {
                attributes.put(keyParts, it)
                attributes.put(keyIsJson, false)
            }[key]
        }
    } // receiveText
}

suspend fun RoutingContext.fetch(key: String): String {
    return call.fetch(key)
}

suspend fun RoutingContext.fetchOrNull(key: String): String? {
    return call.fetchOrNull(key)
}

suspend fun RoutingContext.fetchOrThrow(key: String): String {
    return call.fetchOrThrow(key)
}

fun RoutingContext.fetchGet(key: String): String {
    return call.parameters[key]!!
}

fun RoutingContext.fetchGetOrNull(key: String): String? {
    return call.parameters[key]
}

fun RoutingContext.fetchGetOrThrow(key: String): String {
    return call.parameters[key] ?: throw RuntimeException(key)
}


suspend fun RoutingContext.fetchPost(key: String): String {
    return fetchPostOrNull(key)!!
}

suspend fun RoutingContext.fetchPostOrThrow(key: String): String {
    return fetchPostOrNull(key) ?: throw RuntimeException(key)
}

fun RoutingContext.isJsonData(): Boolean {
    return ContentType.Application.Json == call.request.contentType()
            || (keyIsJson in call.attributes && call.attributes[keyIsJson])
            || (keyJsonElement in call.attributes)
}

suspend fun RoutingContext.isJsonString(key: String): Boolean {
    if (!isJsonData()) return true
    val data = if (keyJsonObject in call.attributes) {
        call.attributes[keyJsonObject]
    } else {
        Json.parseToJsonElement(call.receiveText()).jsonObject.also {
            call.attributes.put(keyJsonObject, it)
            call.attributes.put(keyIsJson, true)
        }
    }
    return data[key] is JsonPrimitive
}

suspend fun RoutingContext.isJsonObject(key: String): Boolean {
    if (!isJsonData()) return false
    val data = if (call.attributes.contains(keyJsonObject)) {
        call.attributes[keyJsonObject]
    } else {
        Json.parseToJsonElement(call.receiveText()).jsonObject.also {
            call.attributes.put(keyJsonObject, it)
        }
    }
    return data[key] is JsonObject
}

suspend fun RoutingContext.isJsonArray(key: String): Boolean {
    if (!isJsonData()) return false
    val data = if (call.attributes.contains(keyJsonObject)) {
        call.attributes[keyJsonObject]
    } else {
        Json.parseToJsonElement(call.receiveText()).jsonObject.also {
            call.attributes.put(keyJsonObject, it)
        }
    }
    return data[key] is JsonArray
}

suspend fun RoutingContext.fetchPostJsonString(key: String): String {
    val data = if (call.attributes.contains(keyJsonObject)) {
        call.attributes[keyJsonObject]
    } else {
        Json.parseToJsonElement(call.receiveText()).jsonObject.also {
            call.attributes.put(keyJsonObject, it)
        }
    }
    return data[key].asString
}

suspend fun RoutingContext.fetchPostJsonElement(key: String): JsonElement {
    val data = if (call.attributes.contains(keyJsonObject)) {
        call.attributes[keyJsonObject]
    } else {
        Json.parseToJsonElement(call.receiveText()).jsonObject.also {
            call.attributes.put(keyJsonObject, it)
        }
    }
    return data[key]!!
}

suspend fun RoutingContext.fetchPostJsonObject(key: String): JsonObject {
    val data = if (call.attributes.contains(keyJsonObject)) {
        call.attributes[keyJsonObject]
    } else {
        Json.parseToJsonElement(call.receiveText()).jsonObject.also {
            call.attributes.put(keyJsonObject, it)
        }
    }
    return data[key].asJsonObject
}

suspend fun RoutingContext.fetchPostJsonObjectOrNull(key: String): JsonObject? {
    val data = if (call.attributes.contains(keyJsonObject)) {
        call.attributes[keyJsonObject]
    } else {
        Json.parseToJsonElement(call.receiveText()).jsonObject.also {
            call.attributes.put(keyIsJson, true)
            call.attributes.put(keyJsonObject, it)
        }
    }
    return data[key].asJsonObjectOrNull
}

suspend fun RoutingContext.fetchPostJsonElementOrNull(): JsonElement? {
    return runCatching {
        if (call.attributes.contains(keyJsonObject)) {
            call.attributes[keyJsonObject]
        } else if (call.attributes.contains(keyJsonArray)) {
            call.attributes[keyJsonArray]
        } else if (call.attributes.contains(keyJsonElement)) {
            call.attributes[keyJsonElement]
        } else {
            Json.parseToJsonElement(call.receiveText()).also {
                call.attributes.put(keyJsonElement, it)
                if (it is JsonObject) {
                    call.attributes.put(keyJsonObject, it)
                } else if (it is JsonArray) {
                    call.attributes.put(keyJsonArray, it)
                }
            }
        }
    }.getOrNull()
}

suspend fun RoutingContext.fetchPostJsonArray(key: String): JsonArray {
    val data = if (call.attributes.contains(keyJsonObject)) {
        call.attributes[keyJsonObject]
    } else {
        Json.parseToJsonElement(call.receiveText()).jsonObject.also {
            call.attributes.put(keyJsonObject, it)
            call.attributes.put(keyIsJson, true)
        }
    }
    return data[key].asJsonArray
}

suspend fun RoutingContext.fetchPostOrNull(key: String): String? {
    return call.fetchPostOrNull(key)
}

@KtorDsl
fun Routing.getOrPost(path: String, body: suspend RoutingContext.() -> Unit) {
    route(path) {
        get(body)
        post(body)
    }
}

@KtorDsl
fun Routing.getOrPost(path: Regex, body: suspend RoutingContext.() -> Unit) {
    route(path) {
        get(body)
        post(body)
    }
}