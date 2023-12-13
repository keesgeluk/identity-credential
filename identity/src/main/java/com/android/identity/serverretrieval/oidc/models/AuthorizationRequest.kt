package com.android.identity.serverretrieval.oidc.models

import kotlinx.serialization.Serializable
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Serializable
data class AuthorizationRequest(
    val clientId: String,
    val scope: String,
    val redirectUri: String,
    val responseType: String,
    val loginHint: String,
) {
    fun toUrlQuery(): String {
        val encodedScope = URLEncoder.encode(scope, StandardCharsets.UTF_8.name())
        val encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.name())
        return "?client_id=$clientId&scope=$encodedScope&redirect_uri=$encodedRedirectUri&response_type=$responseType&login_hint=$loginHint"
    }

    companion object {
        fun fromUrl(url: String): AuthorizationRequest {
            var clientId = ""
            var scope = ""
            var redirectUri = ""
            var responseType = ""
            var loginHint = ""

            val decoded = URLDecoder.decode(url, StandardCharsets.UTF_8.name())
            val queryString = decoded.split("?").last()
            val parametersAndValues = queryString.split("&")
            parametersAndValues.forEach {
                val parameterAndValue = it.split("=")
                when (parameterAndValue[0]) {
                    "client_id" -> clientId = parameterAndValue[1]
                    "scope" -> scope = parameterAndValue[1]
                    "redirect_uri" -> redirectUri = parameterAndValue[1]
                    "response_type" -> responseType = parameterAndValue[1]
                    "login_hint" -> loginHint = parameterAndValue[1]
                }
            }
            return AuthorizationRequest(
                clientId,
                scope,
                redirectUri,
                responseType,
                loginHint
            )
        }
    }
}