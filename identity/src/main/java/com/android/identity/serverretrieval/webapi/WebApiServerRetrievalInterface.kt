package com.android.identity.serverretrieval.webapi

import com.android.identity.serverretrieval.webapi.models.ServerRequest
import com.android.identity.serverretrieval.webapi.models.ServerResponse

interface WebApiServerRetrievalInterface {
    fun serverRetrieval(serverRequest: ServerRequest): ServerResponse
}