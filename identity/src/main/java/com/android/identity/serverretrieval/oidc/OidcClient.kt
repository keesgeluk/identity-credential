package com.android.identity.serverretrieval.oidc

import com.android.identity.serverretrieval.ServerRetrievalInformation
import com.android.identity.serverretrieval.oidc.models.AuthorizationRequest
import com.android.identity.serverretrieval.oidc.models.IdToken
import com.android.identity.serverretrieval.oidc.models.Jwt
import com.android.identity.serverretrieval.oidc.models.LoginHint
import com.android.identity.serverretrieval.oidc.models.RegistrationRequest
import com.android.identity.serverretrieval.oidc.models.TokenRequest
import com.android.identity.util.Timestamp
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey

class OidcClient(
    private val serverRetrievalInformation: ServerRetrievalInformation,
    private val serverRetrievalInterface: OidcServerRetrievalInterface,
    private val privateKey: ECPrivateKey
) {
    fun processServerRetrieval(): Map<String, String> {
        // step 1
        val configuration = serverRetrievalInterface.configuration()

        // step 2
        val registrationRequest = RegistrationRequest(
            redirect_uris = listOf("http://127.0.0.1:56464/callback"), // TODO
            scope = configuration.scopes_supported.joinToString(separator = " ")// request everything...
        )
        val registrationResponse = serverRetrievalInterface.clientRegistration(registrationRequest)

        // step 3
        val authorizationRequest = AuthorizationRequest(
            clientId = registrationResponse.client_id,
            scope = registrationRequest.scope,
            redirectUri = registrationRequest.redirect_uris.first(),
            responseType = "code",
            loginHint = LoginHint(
                id = serverRetrievalInformation.serverRetrievalToken,
                issuedAt = Timestamp.now().toEpochMilli() / 1000,
                expirationTime = (Timestamp.now()
                    .toEpochMilli() / 1000) + (30 * 24 * 60 * 60) // add 30 days
            ).encode(privateKey)
        )
        val authorizationResponse = serverRetrievalInterface.authorization(authorizationRequest)

        // step 4
        val tokenRequest = TokenRequest(
            grantType = "authorization_code",
            code = authorizationResponse.query.code,
            redirectUri = registrationRequest.redirect_uris.first(),
            clientId = registrationResponse.client_id,
            clientSecret = registrationResponse.client_secret
        )
        val tokenResponse = serverRetrievalInterface.getIdToken(tokenRequest)

        // step 5
        val validateIdTokenResponse = serverRetrievalInterface.validateIdToken()

        // TODO validate chain (use TrustManager?)
        val certicate = validateIdTokenResponse.keys.first().parseCertificateChain().first()

        val publicKey = certicate.publicKey as ECPublicKey

        if (!Jwt.verify(tokenResponse.idToken, publicKey) ||
            !Jwt.verify(tokenResponse.accessToken, publicKey)
        ) {
            throw Exception("The token response could not be verified")
        }
        return IdToken.decode(tokenResponse.idToken).claims
    }
}