package com.android.identity.serverretrieval.webapi

import com.android.identity.credentialtype.knowntypes.DrivingLicense
import com.android.identity.serverretrieval.ServerRetrievalInformation
import com.android.identity.serverretrieval.webapi.models.ItemsRequest
import com.android.identity.serverretrieval.webapi.models.ServerRequest
import com.android.identity.serverretrieval.webapi.models.ServerResponse

class WebApiClient(
    private val serverRetrievalInformation: ServerRetrievalInformation,
    private val serverRetrievalInterface: WebApiServerRetrievalInterface
) {
    private val drivingLicensInfo = DrivingLicense.getCredentialType().mdocCredentialType!!
    fun serverRetrieval(): ServerResponse {
        val serverResponse = serverRetrievalInterface.serverRetrieval(
            ServerRequest(
                version = "1.0",
                token = serverRetrievalInformation.serverRetrievalToken,
                docRequests = listOf(ItemsRequest(
                    docType = drivingLicensInfo.docType,
                    nameSpaces = drivingLicensInfo.namespaces.associateBy { it.namespace }
                        .map {
                            Pair(
                                it.key,
                                it.value.dataElements.map { el ->
                                    Pair(
                                        el.attribute.identifier,
                                        false
                                    )
                                }.toMap()
                            )
                        }.toMap()
                )
                )
            )
        )
        return serverResponse
    }
}