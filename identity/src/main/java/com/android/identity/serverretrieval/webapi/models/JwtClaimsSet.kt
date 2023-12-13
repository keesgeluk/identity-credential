package com.android.identity.serverretrieval.webapi.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import java.security.cert.X509Certificate
import java.security.interfaces.ECPrivateKey

@Serializable
data class JwtClaimsSet(
    val doctype: String,
    val namespaces: Map<String, Map<String, String>>, // NameSpace, DataElementIdentifier, DataElementValue
    val errors: Map<String, Map<String, Int>>? // Namespace, DataElementIdentifier, ErrorCode
) {
    companion object {
        fun decode(rawJwt: String): JwtClaimsSet {
            return Json.decodeFromJsonElement(Jwt.decode(rawJwt).payload)
        }
    }

    fun encode(privateKey: ECPrivateKey, certificateChain: List<X509Certificate>): String {
        return Jwt.encode(Json.encodeToJsonElement(this), privateKey, certificateChain)
    }
}