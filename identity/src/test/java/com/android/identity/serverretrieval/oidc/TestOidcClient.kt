package com.android.identity.serverretrieval.oidc

import com.android.identity.serverretrieval.ServerRetrievalInformation
import com.android.identity.serverretrieval.TestKeysAndCertificates
import org.junit.Test

class TestOidcClient {

    @Test
    fun testClient() {
        // note: here the client communicates directly with the server implementation, without the http layer
        val baseUrl = "https://utopiadot.gov"
        val response = OidcClient(
            ServerRetrievalInformation(1, baseUrl, "testToken"),
            OidcServer(
                baseUrl,
                TestKeysAndCertificates.jwtSignerPrivateKey,
                listOf(
                    TestKeysAndCertificates.jwtSignerCertificate,
                    TestKeysAndCertificates.caCertificate
                )
            ),
            TestKeysAndCertificates.clientPrivateKey
        ).processServerRetrieval()
        for (kvp in response) {
            println("\"${kvp.key}\":\"${kvp.value}\"")
        }

    }
}
