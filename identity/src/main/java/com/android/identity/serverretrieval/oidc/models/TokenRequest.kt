package com.android.identity.serverretrieval.oidc.models

import kotlinx.serialization.Serializable
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Serializable
data class TokenRequest(
    val grantType: String,
    val code: String,
    val redirectUri: String,
    val clientId: String,
    val clientSecret: String,
) {
    fun toUrlQuery(): String {
        val encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.name())
        return "?grant_type=$grantType&code=$code&redirect_uri=$encodedRedirectUri&client_id=$clientId&client_secret=$clientSecret"
    }

    companion object {
        fun fromUrl(url: String): TokenRequest {
            var grantType = ""
            var code = ""
            var redirectUri = ""
            var clientId = ""
            var clientSecret = ""

            val decoded = URLDecoder.decode(url, StandardCharsets.UTF_8.name())
            val queryString = decoded.split("?").last()
            val parametersAndValues = queryString.split("&")
            parametersAndValues.forEach {
                val parameterAndValue = it.split("=")
                when (parameterAndValue[0]) {
                    "grant_type" -> grantType = parameterAndValue[1]
                    "code" -> code = parameterAndValue[1]
                    "redirect_uri" -> redirectUri = parameterAndValue[1]
                    "client_id" -> clientId = parameterAndValue[1]
                    "client_secret" -> clientSecret = parameterAndValue[1]
                }
            }
            return TokenRequest(grantType, code, redirectUri, clientId, clientSecret)
        }
    }
}