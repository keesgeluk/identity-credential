package com.android.identity.serverretrieval.webapi.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class ServerRequest(
    val version: String,
    val token: String,
    val docRequests: List<ItemsRequest>
){
    companion object{
        fun decode(jsonString: String): ServerRequest {
            return Json.Default.decodeFromString(jsonString)
        }
    }
    fun encode(): String{
        return Json.Default.encodeToString(this)
    }
}
