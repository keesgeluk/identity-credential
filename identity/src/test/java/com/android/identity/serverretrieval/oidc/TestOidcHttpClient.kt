package com.android.identity.serverretrieval.oidc

import com.android.identity.serverretrieval.ServerRetrievalInformation
import com.android.identity.serverretrieval.TestKeysAndCertificates

class TestOidcHttpClient {

    /**
     * This is an integration test and it supposes that the serverretrieval servlet is running.
     *
     * This servlet can be started with the command "gradlew serverretrieval:tomcatrun" executed in
     * a command prompt in the root of the project
     *
     * Uncomment "@Test" to run this test
     */

    //@Test
    fun testHttpClient() {
        val baseUrl = "http://localhost:8080/serverretrieval"
        val serverRetrievalInformation = ServerRetrievalInformation(1, baseUrl, "testToken")
        val response = OidcClient(
            serverRetrievalInformation,
            OidcHttpClient(serverRetrievalInformation),
            TestKeysAndCertificates.clientPrivateKey
        ).processServerRetrieval()
        for (kvp in response) {
            println("\"${kvp.key}\":\"${kvp.value}\"")
        }
    }
}