package com.android.identity.serverretrieval.webapi.models

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import java.security.Signature
import java.security.cert.X509Certificate
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.util.Base64

class Jwt private constructor(
    val header: JwtHeader,
    val payload: JsonElement,
    val signature: ByteArray
) {
    companion object {

        // currently only SHA256withECDSA is supported/needed
        private const val SIGNATURE_ALGORITHM = "SHA256withECDSA"
        fun verify(rawJwt: String): Boolean {
            val jwt = decode(rawJwt)
            val signatureAlgorithm = Signature.getInstance(SIGNATURE_ALGORITHM)
            signatureAlgorithm.initVerify(jwt.header.parseCertificateChain().first().publicKey as ECPublicKey)
            signatureAlgorithm.update(rawJwt.substringBeforeLast(".").toByteArray())
            return signatureAlgorithm.verify(jwt.signature)
        }

        fun decode(rawJwt: String): Jwt {
            val decoded = rawJwt.split(".").map { Base64.getUrlDecoder().decode(it) }
            val header: JwtHeader = Json.Default.decodeFromString(String(decoded[0]))
            val payload: JsonElement = Json.Default.decodeFromString(String(decoded[1]))
            val signature = decoded[2]
            return Jwt(header, payload, signature)
        }

        fun encode(
            payload: JsonElement,
            privateKey: ECPrivateKey,
            certificateChain: List<X509Certificate>
        ): String {
            // create and encode header
            val headerJson = JwtHeader(
                algorithm ="ES256",
                type = "JWT",
                certificateChain = certificateChain.map { String(Base64.getEncoder().encode(it.encoded)) }
            )
            val headerBase64 = String(Base64.getUrlEncoder().encode(headerJson.encode().toByteArray()))

            // encode payload
            val payloadBase64 =
                String(Base64.getUrlEncoder().encode(payload.toString().toByteArray()))

            // create and encode signature
            val signatureAlgorithm = Signature.getInstance(SIGNATURE_ALGORITHM)
            signatureAlgorithm.initSign(privateKey)
            signatureAlgorithm.update("$headerBase64.$payloadBase64".toByteArray())
            val newSignature = signatureAlgorithm.sign()
            val signatureBase64 = String(Base64.getUrlEncoder().encode(newSignature))

            // return the encoded result
            return "$headerBase64.$payloadBase64.$signatureBase64"
        }
    }
}