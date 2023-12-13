package com.android.identity.serverretrieval.oidc.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class TokenResponse(
    @SerialName("id_token") val idToken: String,
    @SerialName("access_token")val accessToken: String,
    @SerialName("expires_in")val expiresIn: Int,
    @SerialName("token_type")val tokenType: String
){
    companion object{
        fun decode(jsonString: String): TokenResponse {
            return Json.Default.decodeFromString(jsonString)
        }
    }
    fun encode(): String{
        return Json.Default.encodeToString(this)
    }
}