package com.android.identity.serverretrieval.oidc.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class RegistrationRequest(
    val redirect_uris: List<String>,
    val scope: String
){
    companion object{
        fun decode(jsonString: String): RegistrationRequest{
            return Json.Default.decodeFromString(jsonString)
        }
    }
    fun encode(): String{
        return Json.Default.encodeToString(this)
    }
}