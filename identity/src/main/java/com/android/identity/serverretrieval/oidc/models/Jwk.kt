package com.android.identity.serverretrieval.oidc.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.io.ByteArrayInputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Base64

@Serializable
data class Jwk(
    @SerialName("kty") val keyType: String,
    @SerialName("use") val usage: String,
    @SerialName("alg") val algorithm: String,
    @SerialName("x5c") val certificateChain: List<String>
) {
    fun parseCertificateChain(): List<X509Certificate> {
        return certificateChain.map {
            val bytes = Base64.getDecoder().decode(it)
            CertificateFactory.getInstance("X509")
                .generateCertificate(ByteArrayInputStream(bytes)) as X509Certificate
        }
    }
}