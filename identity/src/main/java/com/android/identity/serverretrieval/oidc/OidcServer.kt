package com.android.identity.serverretrieval.oidc

import com.android.identity.credential.NameSpacedData
import com.android.identity.credentialtype.CredentialAttributeType
import com.android.identity.credentialtype.knowntypes.DrivingLicense
import com.android.identity.serverretrieval.SampleDrivingLicense
import com.android.identity.serverretrieval.oidc.models.AccessToken
import com.android.identity.serverretrieval.oidc.models.AuthorizationHeaders
import com.android.identity.serverretrieval.oidc.models.AuthorizationQuery
import com.android.identity.serverretrieval.oidc.models.AuthorizationQueryCode
import com.android.identity.serverretrieval.oidc.models.AuthorizationRequest
import com.android.identity.serverretrieval.oidc.models.AuthorizationResponse
import com.android.identity.serverretrieval.oidc.models.IdToken
import com.android.identity.serverretrieval.oidc.models.Jwk
import com.android.identity.serverretrieval.oidc.models.OpenIdConfiguration
import com.android.identity.serverretrieval.oidc.models.RegistrationRequest
import com.android.identity.serverretrieval.oidc.models.RegistrationResponse
import com.android.identity.serverretrieval.oidc.models.TokenRequest
import com.android.identity.serverretrieval.oidc.models.TokenResponse
import com.android.identity.serverretrieval.oidc.models.ValidateIdTokenResponse
import com.android.identity.util.Timestamp
import java.net.URL
import java.security.cert.X509Certificate
import java.security.interfaces.ECPrivateKey
import java.util.Base64
import java.util.UUID

class OidcServer(
    private val baseUrl: String,
    private val privateKey: ECPrivateKey,
    private val certificateChain: List<X509Certificate>
) : OidcServerRetrievalInterface {
    private val drivingLicenseInfo = DrivingLicense.getCredentialType().mdocCredentialType!!

    private val mdlElements =
        DrivingLicense.getCredentialType().mdocCredentialType?.namespaces?.map { ns ->
            ns.dataElements.map { "${ns.namespace}:${it.attribute.identifier}" }
        }?.flatten()!!

    private val openIdConfiguration = OpenIdConfiguration
        .Builder(baseUrl)
        .addScopes(mdlElements)
        .addClaims(mdlElements)
        .build()

    private var lastRegistrationResponse: RegistrationResponse? = null // TODO caching etc.

    override fun configuration(): OpenIdConfiguration {
        return openIdConfiguration
    }

    override fun clientRegistration(registrationRequest: RegistrationRequest): RegistrationResponse {
        val response = RegistrationResponse(
            client_id = "com.iso.mdocreader-" + UUID.randomUUID().toString(),
            client_id_issued_at = Timestamp.now().toEpochMilli() / 1000,
            client_secret = UUID.randomUUID().toString(),
            client_secret_expires_at = 0,
            grant_types = listOf("authorization_code"),
            client_name = UUID.randomUUID().toString(),
            client_uri = null,
            logo_uri = null,
            redirect_uris = registrationRequest.redirect_uris,
            scope = registrationRequest.scope
        )
        lastRegistrationResponse = response
        return response
    }

    override fun authorization(authorizationRequest: AuthorizationRequest): AuthorizationResponse {
        val autorizationId = UUID.randomUUID().toString()
        val code = AuthorizationQueryCode(
            clientId = authorizationRequest.clientId,
            redirectUri = authorizationRequest.redirectUri,
            authorizationId = autorizationId,
            issuedAt = Timestamp.now().toEpochMilli() / 1000,
            expirationTime = (Timestamp.now().toEpochMilli() / 1000) + 600,
            subject = autorizationId
        )
        val headers = AuthorizationHeaders(
            cacheControl = listOf("no-cache"),
            connection = listOf("Keep-Alive"),
            accept = listOf("*/*"),
            acceptEncoding = listOf("gzip, deflate"),
            cookie = listOf("idsrv"), // TODO
            host = listOf(URL(baseUrl).host),
            referer = listOf(
                baseUrl+ "/connect/authorize/callback" + AuthorizationRequest
                    (
                    clientId = lastRegistrationResponse?.client_id!!,
                    scope = authorizationRequest.scope,
                    redirectUri = "com.company.isomdocreader://login", // TODO
                    responseType = "code",
                    loginHint = authorizationRequest.loginHint
                ).toUrlQuery()
            ), // TODO
            xOriginalProtocol = listOf("http"),
            xOriginalFor = listOf("127.0.0.1:58873") // TODO
        )

        return AuthorizationResponse(
            AuthorizationQuery(
                code = code.encode(privateKey),
                scope = authorizationRequest.scope
            ),
            headers
        )
    }

    override fun getIdToken(tokenRequest: TokenRequest): TokenResponse {
        return TokenResponse(
            idToken = IdToken(
                issuer = baseUrl,
                issuedAt = Timestamp.now().toEpochMilli() / 1000,
                expirationTime = (Timestamp.now().toEpochMilli() / 1000) + 300,
                audience = baseUrl + "/resources",
                subject = "1",
                docType = drivingLicenseInfo.docType,
                claims = getDataElementsPerNamespace("TODO")
            ).encode(privateKey),
            accessToken = AccessToken(
                clientId = lastRegistrationResponse?.client_name!!,
                issuer = baseUrl,
                issuedAt = Timestamp.now().toEpochMilli() / 1000,
                expirationTime = (Timestamp.now().toEpochMilli() / 1000) + 300,
                audience = baseUrl + "/resources",
                subject = "1"
            ).encode(privateKey),
            expiresIn = 300,
            tokenType = "Bearer"
        )
    }

    override fun validateIdToken(): ValidateIdTokenResponse {
        return ValidateIdTokenResponse(
            keys = listOf(
                Jwk(
                    keyType = "EC",
                    usage = "sig",
                    algorithm = "ES256",
                    certificateChain = certificateChain.map {
                        String(
                            Base64.getEncoder().encode(it.encoded)
                        )
                    })
            )
        )
    }

    private fun getDataElementsPerNamespace(token: String): Map<String, String> {
        val result: MutableMap<String, String> = mutableMapOf()
        val nameSpacedData = getNameSpacedDataByToken(token)
        val elements = lastRegistrationResponse?.scope?.split(" ")?.filter { it.contains(":") }!!
        elements.forEach { element ->
            val namespaceAndElement = element.split(":")
            result[element] =getElementValue(nameSpacedData, namespaceAndElement[0], namespaceAndElement[1])
        }
        return result
    }

    private fun getNameSpacedDataByToken(token: String): NameSpacedData {
        // TODO: for now sample data
        return SampleDrivingLicense.data
    }

    private fun getElementValue(
        nameSpacedData: NameSpacedData,
        namespace: String,
        element: String
    ): String {
        val datatype =
            drivingLicenseInfo.namespaces.find { it.namespace == namespace }?.dataElements?.find { it.attribute.identifier == element }?.attribute?.type!!
        return when (datatype) {
            is CredentialAttributeType.BOOLEAN -> nameSpacedData.getDataElementBoolean(
                namespace,
                element
            ).toString()

            is CredentialAttributeType.PICTURE -> String(
                Base64.getEncoder()
                    .encode(nameSpacedData.getDataElementByteString(namespace, element))
            )

            is CredentialAttributeType.NUMBER,
            is CredentialAttributeType.IntegerOptions -> nameSpacedData.getDataElementNumber(
                namespace,
                element
            ).toString()

            is CredentialAttributeType.COMPLEX_TYPE -> ""//convertCborToJson(nameSpacedData.getDataElement(namespace, element))
            else -> nameSpacedData.getDataElementString(namespace, element)
        }
    }

}