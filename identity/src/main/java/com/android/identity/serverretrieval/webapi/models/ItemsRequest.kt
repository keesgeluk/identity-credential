package com.android.identity.serverretrieval.webapi.models

import kotlinx.serialization.Serializable

@Serializable
data class ItemsRequest(
    val docType: String,
    val nameSpaces: Map<String, Map<String, Boolean>>  // NameSpace, DataElementIdentifier, IntentToRetain
)