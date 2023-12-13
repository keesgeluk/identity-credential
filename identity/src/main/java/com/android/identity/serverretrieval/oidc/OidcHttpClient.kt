package com.android.identity.serverretrieval.oidc

import com.android.identity.serverretrieval.ServerRetrievalInformation
import com.android.identity.serverretrieval.oidc.models.AuthorizationRequest
import com.android.identity.serverretrieval.oidc.models.AuthorizationResponse
import com.android.identity.serverretrieval.oidc.models.OpenIdConfiguration
import com.android.identity.serverretrieval.oidc.models.RegistrationRequest
import com.android.identity.serverretrieval.oidc.models.RegistrationResponse
import com.android.identity.serverretrieval.oidc.models.TokenRequest
import com.android.identity.serverretrieval.oidc.models.TokenResponse
import com.android.identity.serverretrieval.oidc.models.ValidateIdTokenResponse
import com.android.identity.util.Logger
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class OidcHttpClient(
    private val serverRetrievalInformation: ServerRetrievalInformation
) : OidcServerRetrievalInterface {
    private val TAG = "OidcHttpClient"
    private lateinit var openIdConfiguration: OpenIdConfiguration
    override fun configuration(): OpenIdConfiguration {
        val response = doGet(serverRetrievalInformation.issuerURL + "/.well-known/openid-configuration")
        openIdConfiguration = OpenIdConfiguration.decode(response)
        return openIdConfiguration
    }

    override fun clientRegistration(registrationRequest: RegistrationRequest): RegistrationResponse {
        val response = doPost(openIdConfiguration.registration_endpoint, registrationRequest.encode())
        return RegistrationResponse.decode(response)
    }

    override fun authorization(authorizationRequest: AuthorizationRequest): AuthorizationResponse {
        val response = doGet(openIdConfiguration.authorization_endpoint + authorizationRequest.toUrlQuery())
        return AuthorizationResponse.decode(response)
    }

    override fun getIdToken(tokenRequest: TokenRequest): TokenResponse {
        val response = doPost(openIdConfiguration.token_endpoint, tokenRequest.toUrlQuery())
        return TokenResponse.decode(response)
    }
    override fun validateIdToken(): ValidateIdTokenResponse {
        val response = doGet(openIdConfiguration.jwks_uri)
        return ValidateIdTokenResponse.decode(response)
    }
    private fun doGet(url: String): String{
        Logger.d(TAG, "Open ID Connect call. Url: $url")
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString()).body()
        Logger.d(TAG, "Response: $response")
        return response
    }
    private fun doPost(url: String, requestBody: String): String{
        Logger.d(TAG, "Open ID Connect call. Url: $url")
        Logger.d(TAG, "Request Body: $requestBody")
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString()).body()
        Logger.d(TAG, "Response: $response")
        return response
    }
}