package com.android.identity.serverretrieval.webapi

import com.android.identity.serverretrieval.ServerRetrievalInformation
import com.android.identity.serverretrieval.webapi.models.Jwt
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TestWebApiHttpClient {

    private val prettyPrintJson = Json() {
        prettyPrint = true
    }

    /**
     * This is an integration test and it supposes that the serverretrieval servlet is running.
     *
     * This servlet can be started with the command "gradlew serverretrieval:tomcatrun" executed in
     * a command prompt in the root of the project
     *
     * Uncomment "@Test" to run this test
     */

    //@Test
    fun testClient() {
        val baseUrl = "http://localhost:8080/serverretrieval"
        val serverRetrievalInformation = ServerRetrievalInformation(1, baseUrl, "testToken")
        val response = WebApiClient(
            serverRetrievalInformation,
            WebApiHttpClient(serverRetrievalInformation)
        ).serverRetrieval()
        for (document in response.documents!!) {
            assert(Jwt.verify(document))
            println("Document:\n" + prettyPrintJson.encodeToString(Jwt.decode(document).payload))
        }
    }
}