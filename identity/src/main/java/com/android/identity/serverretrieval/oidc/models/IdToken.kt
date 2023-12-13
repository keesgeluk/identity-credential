package com.android.identity.serverretrieval.oidc.models

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.put
import java.security.interfaces.ECPrivateKey

data class IdToken(
    val issuer: String,
    val issuedAt: Long,
    val expirationTime: Long,
    val audience: String,
    val subject: String,
    val docType: String,
    val claims: Map<String, String>
) {
    companion object {
        fun decode(rawJwt: String): IdToken {
            val json = Jwt.decode(rawJwt).payload
            val jsonObject: JsonObject = Json.Default.decodeFromJsonElement(json)
            var issuer = ""
            var issuedAt: Long = 0
            var expirationTime: Long = 0
            var audience = ""
            var subject = ""
            var docType = ""
            val claims: MutableMap<String, String> = mutableMapOf()
            for (key in jsonObject.keys) {
                when (key.lowercase()) {
                    "iss" -> issuer = readString(jsonObject[key]!!)
                    "iat" -> issuedAt = jsonObject[key]?.jsonPrimitive?.long!!
                    "exp" -> expirationTime = jsonObject[key]?.jsonPrimitive?.long!!
                    "aud" -> audience = readString(jsonObject[key]!!)
                    "sub" -> subject = readString(jsonObject[key]!!)
                    "doctype" -> docType = readString(jsonObject[key]!!)
                    else -> claims[key] = readString(jsonObject[key]!!)
                }
            }
            return IdToken(
                issuer,
                issuedAt,
                expirationTime,
                audience,
                subject,
                docType,
                claims
            )
        }

        fun readString(element: JsonElement): String {
            return element.toString().replace("\"", "")
        }
    }

    fun encode(privateKey: ECPrivateKey): String {
        return Jwt.encode(buildJsonObject {
            put("iss", issuer)
            put("iat", issuedAt)
            put("exp", expirationTime)
            put("aud", audience)
            put("sub", subject)
            put("doctype", docType)
            claims.forEach { put(it.key, it.value) }
        }, privateKey)
    }
}