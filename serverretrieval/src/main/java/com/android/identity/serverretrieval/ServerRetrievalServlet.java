package com.android.identity.serverretrieval;

import com.android.identity.serverretrieval.oidc.OidcServer;
import com.android.identity.serverretrieval.oidc.models.AuthorizationRequest;
import com.android.identity.serverretrieval.oidc.models.RegistrationRequest;
import com.android.identity.serverretrieval.oidc.models.TokenRequest;
import com.android.identity.serverretrieval.webapi.WebApiServer;
import com.android.identity.serverretrieval.webapi.models.ServerRequest;
import com.android.identity.util.Logger;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ServerRetrievalServlet extends HttpServlet {
    private static final String TAG = "ServerRetrievalServlet";
    private WebApiServer webApiServer = new WebApiServer(
            TestKeysAndCertificates.INSTANCE.getJwtSignerPrivateKey(),
            List.of(TestKeysAndCertificates.INSTANCE.getJwtSignerCertificate(),
                    TestKeysAndCertificates.INSTANCE.getCaCertificate()));
    private OidcServer oidcServer = new OidcServer(
            "http://localhost:8080/serverretrieval",
            TestKeysAndCertificates.INSTANCE.getJwtSignerPrivateKey(),
            List.of(TestKeysAndCertificates.INSTANCE.getJwtSignerCertificate(),
                    TestKeysAndCertificates.INSTANCE.getCaCertificate()));
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int requestLength = req.getContentLength();
        String requestData = new String(req.getInputStream().readNBytes(requestLength));
        String responseData;
        if (req.getRequestURI().contains("/identity")) {
            responseData = webApiServer.serverRetrieval(ServerRequest.Companion.decode(requestData)).encode();
        } else if (req.getRequestURI().contains("/connect/register")) {
            responseData = oidcServer.clientRegistration(RegistrationRequest.Companion.decode(requestData)).encode();
        } else if (req.getRequestURI().contains("/connect/token")) {
            responseData = oidcServer.getIdToken(TokenRequest.Companion.fromUrl(requestData)).encode();
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getOutputStream().write(responseData.getBytes());
    }

    private String getRemoteHost(HttpServletRequest req) {
        String remoteHost = req.getRemoteHost();
        String forwardedFor = req.getHeader("X-Forwarded-For");
        if (forwardedFor != null) {
            remoteHost = forwardedFor;
        }
        return remoteHost;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Logger.d(TAG, "GET from " + getRemoteHost(req));
        String responseData;
        if (req.getRequestURI().contains(".well-known/openid-configuration")) {
            responseData = oidcServer.configuration().encode();
        } else if (req.getRequestURI().contains("connect/authorize")) {
            responseData = oidcServer.authorization(AuthorizationRequest.Companion.fromUrl(req.getRequestURI())).encode();
        } else if (req.getRequestURI().contains(".well-known/jwks.json")) {
            responseData = oidcServer.validateIdToken().encode();
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.getOutputStream().write(responseData.getBytes());
    }
}
