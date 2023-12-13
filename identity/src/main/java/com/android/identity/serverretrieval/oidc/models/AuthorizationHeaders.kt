package com.android.identity.serverretrieval.oidc.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthorizationHeaders(
    @SerialName("Cache-Control") val cacheControl: List<String>,
    @SerialName("Connection") val connection: List<String>,
    @SerialName("Accept") val accept: List<String>,
    @SerialName("Accept-Encoding") val acceptEncoding: List<String>,
    @SerialName("Cookie") val cookie: List<String>,
    @SerialName("Host") val host: List<String>,
    @SerialName("Referer") val referer: List<String>,
    @SerialName("X-Original-Proto") val xOriginalProtocol: List<String>,
    @SerialName("X-Original-For") val xOriginalFor: List<String>
)