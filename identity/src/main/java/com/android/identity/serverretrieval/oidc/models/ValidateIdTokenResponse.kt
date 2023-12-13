package com.android.identity.serverretrieval.oidc.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class ValidateIdTokenResponse (
    val keys: List<Jwk>
){
    companion object{
        fun decode(jsonString: String): ValidateIdTokenResponse {
            return Json.Default.decodeFromString(jsonString)
        }
    }
    fun encode(): String{
        return Json.Default.encodeToString(this)
    }
}