package com.android.identity.serverretrieval

import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.BasicConstraints
import org.bouncycastle.asn1.x509.Extension
import org.bouncycastle.asn1.x509.KeyUsage
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.cert.X509ExtensionUtils
import org.bouncycastle.cert.bc.BcX509ExtensionUtils
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder
import org.bouncycastle.crypto.util.PublicKeyFactory
import org.bouncycastle.crypto.util.SubjectPublicKeyInfoFactory
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.io.ByteArrayInputStream
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.interfaces.ECPrivateKey
import java.security.spec.ECGenParameterSpec
import java.util.Date

object TestKeysAndCertificates {
    val caCertificate: X509Certificate
    val jwtSignerCertificate: X509Certificate
    val jwtSignerPrivateKey: ECPrivateKey
    val clientPrivateKey: ECPrivateKey

    init {
        java.security.Security.insertProviderAt(BouncyCastleProvider(), 1)
        val extensionUtils: X509ExtensionUtils = BcX509ExtensionUtils()
        val kpg = KeyPairGenerator.getInstance("EC", BouncyCastleProvider())
        kpg.initialize(ECGenParameterSpec("secp256r1"))

        // generate CA certificate
        val keyPairCA = kpg.generateKeyPair()
        val caPublicKeyInfo: SubjectPublicKeyInfo =
            SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(
                PublicKeyFactory.createKey(keyPairCA.public.encoded)
            )
        val nowMillis = System.currentTimeMillis()
        val certBuilderCA = JcaX509v3CertificateBuilder(
            X500Name("CN=JWT CA"),
            BigInteger.ZERO,
            Date(nowMillis),
            Date(nowMillis + 24 * 3600 * 1000),
            X500Name("CN=JWT CA"),
            keyPairCA.public
        ).addExtension(Extension.basicConstraints, true, BasicConstraints(0))
            .addExtension(Extension.keyUsage, true, KeyUsage(KeyUsage.keyCertSign))
            .addExtension(
                Extension.subjectKeyIdentifier,
                false,
                extensionUtils.createSubjectKeyIdentifier(caPublicKeyInfo)
            )
        val signerCA = JcaContentSignerBuilder("SHA256withECDSA").build(keyPairCA.private)
        val caHolder = certBuilderCA.build(signerCA)
        val cf = CertificateFactory.getInstance("X.509")
        val caStream = ByteArrayInputStream(caHolder.encoded)
        caCertificate = cf.generateCertificate(caStream) as X509Certificate

        // generate JTW signer certificate
        val keyPairJwtSigner = kpg.generateKeyPair()
        val jwtSignerPublicKeyInfo: SubjectPublicKeyInfo =
            SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(
                PublicKeyFactory.createKey(keyPairJwtSigner.public.encoded)
            )
        val certBuilderJwtSigner = JcaX509v3CertificateBuilder(
            X500Name("CN=JWT CA"),
            BigInteger.ONE.add(BigInteger.ONE),
            Date(nowMillis),
            Date(nowMillis + 24 * 3600 * 1000),
            X500Name("CN=JWT Signer"),
            keyPairJwtSigner.public
        ).addExtension(Extension.basicConstraints, true, BasicConstraints(0))
            .addExtension(Extension.keyUsage, true, KeyUsage(KeyUsage.digitalSignature))
            .addExtension(
                Extension.authorityKeyIdentifier,
                false,
                extensionUtils.createAuthorityKeyIdentifier(caHolder)
            )
            .addExtension(
                Extension.subjectKeyIdentifier,
                false,
                extensionUtils.createSubjectKeyIdentifier(jwtSignerPublicKeyInfo)
            )
        val jwtSignerHolder = certBuilderJwtSigner.build(signerCA)
        val jwtSignerStream = ByteArrayInputStream(jwtSignerHolder.encoded)
        jwtSignerCertificate = cf.generateCertificate(jwtSignerStream) as X509Certificate
        jwtSignerPrivateKey = keyPairJwtSigner.private as ECPrivateKey
        clientPrivateKey = kpg.generateKeyPair().private as ECPrivateKey // TODO: what key should this be?
    }
}