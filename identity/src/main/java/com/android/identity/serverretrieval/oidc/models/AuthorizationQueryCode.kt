package com.android.identity.serverretrieval.oidc.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import java.security.interfaces.ECPrivateKey

@Serializable
data class AuthorizationQueryCode(
    @SerialName("client_id") val clientId: String,
    @SerialName("redirect_uri") val redirectUri: String,
    @SerialName("auth_id") val authorizationId: String,
    @SerialName("iat") val issuedAt: Long,
    @SerialName("exp") val expirationTime: Long,
    @SerialName("sub") val subject: String
) {
    companion object {
        fun decode(rawJwt: String): AuthorizationQueryCode {
            return Json.decodeFromJsonElement(
                Jwt.decode(
                    rawJwt
                ).payload)
        }
    }

    fun encode(privateKey: ECPrivateKey): String {
        return Jwt.encode(
            Json.encodeToJsonElement(
                this
            ), privateKey
        )
    }
}