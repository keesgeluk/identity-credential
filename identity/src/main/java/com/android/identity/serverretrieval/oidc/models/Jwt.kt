package com.android.identity.serverretrieval.oidc.models

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.security.Signature
import java.security.interfaces.ECPrivateKey
import java.security.interfaces.ECPublicKey
import java.util.Base64

class Jwt private constructor(
    val header: JsonElement,
    val payload: JsonElement,
    val signature: ByteArray
) {
    companion object {

        // currently only SHA256withECDSA is supported/needed
        private const val SIGNATURE_ALGORITHM = "SHA256withECDSA"
        fun verify(rawJwt: String, publicKey: ECPublicKey): Boolean {
            val signatureAlgorithm = Signature.getInstance(SIGNATURE_ALGORITHM)
            signatureAlgorithm.initVerify(publicKey)
            signatureAlgorithm.update(rawJwt.substringBeforeLast(".").toByteArray())
            val signature = Base64.getUrlDecoder().decode(rawJwt.substringAfterLast("."))
            return signatureAlgorithm.verify(signature)
        }
        fun decode(rawJwt: String): Jwt {
            val decoded = rawJwt.split(".").map { Base64.getUrlDecoder().decode(it) }
            val header: JsonElement = Json.Default.decodeFromString(String(decoded[0]))
            val payload: JsonElement = Json.Default.decodeFromString(String(decoded[1]))
            val signature = decoded[2]
            return Jwt(header, payload, signature)
        }
        fun encode(payload: JsonElement,  privateKey: ECPrivateKey): String {
            // create and encode header
            val headerJson = buildJsonObject {
                put("alg", "ES256")
                put("typ", "JWT")
            }.toString()
            val headerBase64 = String(Base64.getUrlEncoder().encode(headerJson.toByteArray()))

            // encode payload
            val payloadBase64 = String(Base64.getUrlEncoder().encode(payload.toString().toByteArray()))

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