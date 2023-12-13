package com.android.identity.serverretrieval.webapi.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.ByteArrayInputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Base64

@Serializable
data class JwtHeader(
    @SerialName("alg") val algorithm: String,
    @SerialName("type") val type: String,
    @SerialName("x5c") val certificateChain: List<String>
) {
    companion object{
        fun decode(jsonString: String): JwtHeader {
            return Json.Default.decodeFromString(jsonString)
        }
    }
    fun encode(): String{
        return Json.Default.encodeToString(this)
    }

    fun parseCertificateChain(): List<X509Certificate> {
        return certificateChain.map {
            val bytes = Base64.getDecoder().decode(it)
            CertificateFactory.getInstance("X509")
                .generateCertificate(ByteArrayInputStream(bytes)) as X509Certificate
        }
    }
}