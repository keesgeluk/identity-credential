package com.android.identity.serverretrieval.webapi

import com.android.identity.serverretrieval.ServerRetrievalInformation
import com.android.identity.serverretrieval.webapi.models.ServerRequest
import com.android.identity.serverretrieval.webapi.models.ServerResponse
import com.android.identity.util.Logger
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class WebApiHttpClient (private val serverRetrievalInformation: ServerRetrievalInformation): WebApiServerRetrievalInterface {

    private val TAG = "WebApiHttpClient"
    override fun serverRetrieval(serverRequest: ServerRequest): ServerResponse {
        val client = HttpClient.newBuilder().build()
        val url = serverRetrievalInformation.issuerURL + "/identity"
        val requestBody = serverRequest.encode()
        Logger.d(TAG, "Web api call. Url: $url")
        Logger.d(TAG, "Request Body: $requestBody")
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()
         val response = client.send(request, HttpResponse.BodyHandlers.ofString()).body()
        Logger.d(TAG, "Response: ${response}")
        return ServerResponse.decode(response)
    }
}