package com.android.identity.serverretrieval.oidc.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import java.security.interfaces.ECPrivateKey

@Serializable
data class AccessToken(
    @SerialName("client_id") val clientId: String,
    @SerialName("iss") val issuer: String,
    @SerialName("iat") val issuedAt: Long,
    @SerialName("exp") val expirationTime: Long,
    @SerialName("aud") val audience: String,
    @SerialName("sub") val subject: String
) {
    companion object {
        fun decode(rawJwt: String): AccessToken {
            return Json.decodeFromJsonElement(
                Jwt.decode(
                    rawJwt
                ).payload)
        }
    }

    fun encode(privateKey: ECPrivateKey): String {
        return Jwt.encode(
            Json.Default.encodeToJsonElement(
                this
            ), privateKey
        )
    }
}