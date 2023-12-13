package com.android.identity.serverretrieval.oidc.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
@Serializable
data class RegistrationResponse(
    val client_id: String,
    val client_id_issued_at: Long,
    val client_secret: String,
    val client_secret_expires_at: Long,
    val grant_types: List<String>,
    val client_name: String,
    val client_uri: String?,
    val logo_uri: String?,
    val redirect_uris: List<String>,
    val scope: String
){
    companion object{
        fun decode(jsonString: String): RegistrationResponse {
            return Json.Default.decodeFromString(jsonString)
        }
    }
    fun encode(): String{
        return Json.Default.encodeToString(this)
    }
}
