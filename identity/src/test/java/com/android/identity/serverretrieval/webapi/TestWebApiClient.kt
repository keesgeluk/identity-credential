package com.android.identity.serverretrieval.webapi

import com.android.identity.serverretrieval.ServerRetrievalInformation
import com.android.identity.serverretrieval.TestKeysAndCertificates
import com.android.identity.serverretrieval.webapi.models.Jwt
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

class TestWebApiClient {

    private val prettyPrintJson = Json() {
        prettyPrint = true
    }

    @Test
    fun testClient() {
        // note: here the client communicates directly with the server implementation, without the http layer
        val response = WebApiClient(
            ServerRetrievalInformation(1, "https://utopiadot.gov", "testToken"),
            WebApiServer(
                TestKeysAndCertificates.jwtSignerPrivateKey,
                listOf(
                    TestKeysAndCertificates.jwtSignerCertificate,
                    TestKeysAndCertificates.caCertificate
                )
            )
        ).serverRetrieval()
        println("Raw result:\n" + response.encode())
        for (document in response.documents!!) {
            assert(Jwt.verify(document))
            println("Document:\n" + prettyPrintJson.encodeToString(Jwt.decode(document).payload))
        }
    }
}