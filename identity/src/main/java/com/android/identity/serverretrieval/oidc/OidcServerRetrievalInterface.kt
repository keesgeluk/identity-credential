package com.android.identity.serverretrieval.oidc

import com.android.identity.serverretrieval.oidc.models.AuthorizationRequest
import com.android.identity.serverretrieval.oidc.models.AuthorizationResponse
import com.android.identity.serverretrieval.oidc.models.OpenIdConfiguration
import com.android.identity.serverretrieval.oidc.models.RegistrationRequest
import com.android.identity.serverretrieval.oidc.models.RegistrationResponse
import com.android.identity.serverretrieval.oidc.models.TokenRequest
import com.android.identity.serverretrieval.oidc.models.TokenResponse
import com.android.identity.serverretrieval.oidc.models.ValidateIdTokenResponse

interface OidcServerRetrievalInterface {
    fun configuration(): OpenIdConfiguration
    fun clientRegistration(registrationRequest: RegistrationRequest): RegistrationResponse
    fun authorization(authorizationRequest: AuthorizationRequest): AuthorizationResponse
    fun getIdToken(tokenRequest: TokenRequest): TokenResponse
    fun validateIdToken(): ValidateIdTokenResponse
}