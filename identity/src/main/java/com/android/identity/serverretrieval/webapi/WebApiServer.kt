package com.android.identity.serverretrieval.webapi

import com.android.identity.credential.NameSpacedData
import com.android.identity.credentialtype.CredentialAttributeType
import com.android.identity.credentialtype.knowntypes.DrivingLicense
import com.android.identity.serverretrieval.SampleDrivingLicense
import com.android.identity.serverretrieval.webapi.models.JwtClaimsSet
import com.android.identity.serverretrieval.webapi.models.ServerRequest
import com.android.identity.serverretrieval.webapi.models.ServerResponse
import java.security.cert.X509Certificate
import java.security.interfaces.ECPrivateKey
import java.util.Base64

class WebApiServer(
    private val privateKey: ECPrivateKey,
    private val certificateChain: List<X509Certificate>
) : WebApiServerRetrievalInterface {

    private val drivingLicenseInfo = DrivingLicense.getCredentialType().mdocCredentialType!!
    override fun serverRetrieval(serverRequest: ServerRequest): ServerResponse {

        val claimsSet = JwtClaimsSet(
            doctype = serverRequest.docRequests.first().docType,
            namespaces = getDataElementsPerNamespace(serverRequest),
            errors = null,
        )
        return ServerResponse(
            version = "1.0",
            documents = listOf(claimsSet.encode(privateKey, certificateChain)),
            documentErrors = null
        )
    }

    private fun getDataElementsPerNamespace(serverRequest: ServerRequest): Map<String, Map<String, String>> {
        val result: MutableMap<String, Map<String, String>> = mutableMapOf()
        val nameSpacedData = getNameSpacedDataByToken(serverRequest.token)
        serverRequest.docRequests.first().nameSpaces.forEach { ns ->
            result[ns.key] = ns.value.keys.map { el -> Pair(el, getElementValue(nameSpacedData, ns.key, el)) }.toMap()
        }
        return result
    }

    private fun getNameSpacedDataByToken(token: String): NameSpacedData {
        // TODO: for now sample data
        return SampleDrivingLicense.data
    }

    private fun getElementValue(nameSpacedData: NameSpacedData, namespace: String, element: String): String{
        val datatype = drivingLicenseInfo.namespaces.find {it.namespace == namespace}?.dataElements?.find { it.attribute.identifier == element }?.attribute?.type!!
        return when (datatype){
            is CredentialAttributeType.BOOLEAN -> nameSpacedData.getDataElementBoolean(namespace, element).toString()
            is CredentialAttributeType.PICTURE -> String(Base64.getEncoder().encode(nameSpacedData.getDataElementByteString(namespace, element)))
            is CredentialAttributeType.NUMBER,
            is CredentialAttributeType.IntegerOptions -> nameSpacedData.getDataElementNumber(namespace, element).toString()
            is CredentialAttributeType.COMPLEX_TYPE -> "" // TODO: convert cbor to json
            else -> nameSpacedData.getDataElementString(namespace, element)
        }
    }
}