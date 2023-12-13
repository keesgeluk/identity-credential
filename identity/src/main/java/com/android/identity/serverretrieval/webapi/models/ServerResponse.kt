package com.android.identity.serverretrieval.webapi.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class ServerResponse(
    val version: String,
    val documents: List<String>?,
    val documentErrors: Map<String, Int>? // DocType, ErrorCode
){
    companion object{
        fun decode(jsonString: String): ServerResponse {
            return Json.Default.decodeFromString(jsonString)
        }
    }
    fun encode(): String{
        return Json.Default.encodeToString(this)
    }
}