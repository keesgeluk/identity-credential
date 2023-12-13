package com.android.identity.serverretrieval.oidc.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class OpenIdConfiguration(
    val issuer: String,
    val jwks_uri: String,
    val authorization_endpoint: String,
    val token_endpoint: String,
    val userinfo_endpoint: String,
    val end_session_endpoint: String,
    val revocation_endpoint: String,
    val introspection_endpoint: String,
    val device_authorization_endpoint: String,
    val registration_endpoint: String,
    val frontchannel_logout_supported: Boolean,
    val frontchannel_logout_session_supported: Boolean,
    val backchannel_logout_supported: Boolean,
    val backchannel_logout_session_supported: Boolean,
    val scopes_supported: List<String>,
    val claims_supported: List<String>,
    val grant_types_supported: List<String>,
    val response_types_supported: List<String>,
    val response_modes_supported: List<String>,
    val token_endpoint_auth_methods_supported: List<String>,
    val subject_types_supported: List<String>,
    val id_token_signing_alg_values_supported: List<String>,
    val code_challenge_methods_supported: List<String>
) {
    companion object {
        fun decode(jsonString: String): OpenIdConfiguration {
            return Json.Default.decodeFromString(jsonString)
        }
    }

    fun encode(): String {
        return Json.Default.encodeToString(this)
    }

    class Builder(
        private val baseUrl: String,
        private val supportedScopes: MutableList<String> = mutableListOf("openid"),
        private val supportedClaims: MutableList<String> = mutableListOf(
            "docType",
            "sub"
        )
    ) {
        fun addScopes(scopes: List<String>) = apply {
            supportedScopes.addAll(scopes)
        }

        fun addClaims(claims: List<String>) = apply {
            supportedClaims.addAll(claims)
        }

        fun build(): OpenIdConfiguration {
            return OpenIdConfiguration(
                issuer = baseUrl,
                jwks_uri = "$baseUrl/.well-known/jwks.json",
                authorization_endpoint = "$baseUrl/connect/authorize",
                token_endpoint = "$baseUrl/connect/token",
                userinfo_endpoint = "$baseUrl/connect/userinfo",
                end_session_endpoint = "$baseUrl/connect/end_session",
                revocation_endpoint = "$baseUrl/connect/revocation",
                introspection_endpoint = "$baseUrl/connect/introspec",
                device_authorization_endpoint = "$baseUrl/connect/deviceauthorization",
                registration_endpoint = "$baseUrl/connect/register",
                frontchannel_logout_supported = true,
                frontchannel_logout_session_supported = true,
                backchannel_logout_supported = true,
                backchannel_logout_session_supported = true,
                scopes_supported = supportedScopes,
                claims_supported = supportedClaims,
                grant_types_supported = listOf(
                    "authorization_code",
                    "client_credentials",
                    "refresh_token",
                    "implicit",
                    "urn:ietf:params:oauth:grant-type:device_code"
                ),
                response_types_supported = listOf(
                    "code",
                    "token",
                    "id_token",
                    "id_token token",
                    "code id_token",
                    "code token",
                    "code id_token token"
                ),
                response_modes_supported = listOf(
                    "form_post",
                    "query",
                    "fragment"
                ),
                token_endpoint_auth_methods_supported = listOf(
                    "client_secret_basic",
                    "client_secret_post"
                ),
                subject_types_supported = listOf("public"),
                id_token_signing_alg_values_supported = listOf("ES256"),
                code_challenge_methods_supported = listOf("plain", "S256")
            )
        }
    }


}