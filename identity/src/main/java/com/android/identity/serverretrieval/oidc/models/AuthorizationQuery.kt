package com.android.identity.serverretrieval.oidc.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizationQuery(val code: String, val scope: String) {
}