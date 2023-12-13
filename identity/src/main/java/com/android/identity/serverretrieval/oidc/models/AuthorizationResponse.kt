package com.android.identity.serverretrieval.oidc.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class AuthorizationResponse(
    @SerialName("Query") val query: AuthorizationQuery,
    @SerialName("Headers") val headers: AuthorizationHeaders
){

    companion object{
        fun decode(jsonString: String): AuthorizationResponse {
            return Json.Default.decodeFromString(jsonString)
        }
    }
    fun encode(): String{
        return Json.Default.encodeToString(this)
    }
}
