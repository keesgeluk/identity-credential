package com.android.identity.serverretrieval.oidc.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import java.security.interfaces.ECPrivateKey

@Serializable
data class LoginHint(
    @SerialName("id")val id: String,
    @SerialName("iat") val issuedAt: Long,
    @SerialName("exp") val expirationTime: Long,
) {
    companion object {
        fun decode(rawJwt: String): LoginHint {
            return Json.decodeFromJsonElement(Jwt.decode(rawJwt).payload)
        }
    }

    fun encode(privateKey: ECPrivateKey): String {
        return Jwt.encode(Json.encodeToJsonElement(this), privateKey)
    }
}