package com.example.testing_java_code;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.signers.Ed448Signer;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.security.interfaces.RSAPrivateKey;
import java.util.Arrays;

// Alguns links úteis
// Source: https://www.tabnine.com/code/java/methods/org.bouncycastle.crypto.Signer/init
// Source: https://www.tabnine.com/web/assistant/code/rs/5c7447e649efcb0001ef3b06#L55

public class VerifyEd448Signature {
    // Essa variável é usada como argumento na instanciação do Signer do Ed448, na função verifyEd448Signature
    private static final byte[] EMPTY_CONTEXT = new byte[0];

    // Extrai a chave pública de um certificado no formato PEM
    public static AsymmetricKeyParameter getPublicKeyFromPEMCertificate(Reader reader) throws IOException {
        try (PEMParser pem = new PEMParser(reader)) {
            Object pemContent = pem.readObject();
            if (pemContent instanceof X509CertificateHolder) {
                X509CertificateHolder cert = (X509CertificateHolder) pemContent;
                SubjectPublicKeyInfo spki = cert.getSubjectPublicKeyInfo();
                return PublicKeyFactory.createKey(spki);
            } else {
                throw new IllegalArgumentException("Unsupported certificate format '" +
                        pemContent.getClass().getSimpleName() + '"');
            }
        }
    }

    // Lê a assinatura no formato InputStream e a retorna em bytes
    public static byte[] readSignature(InputStream sigStream) throws IOException {
        byte[] signature = readBytes(sigStream);
        return signature;
    }

    // Lê uma InputStream e a retorna em bytes
    public static byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    // Verifica uma assinatura Ed448 para uma mensagem, e retorna o resultado da verificação:
    // 'true' (se é válida) ou 'false' (se é inválida)
    public static boolean verifyEd448Signature(AsymmetricKeyParameter publicKey, String message, byte[] expectedSignature)
            throws Exception {
        byte[] messageBytes = message.getBytes();
        Signer signer = new Ed448Signer(EMPTY_CONTEXT);
        signer.init(false, publicKey);
        signer.update(messageBytes, 0, messageBytes.length);
        // É necessário guardar o resultado da verificação numa variável antes, caso contrário não
        // funciona (dá sempre 'false')
        boolean valor = signer.verifySignature(expectedSignature);
        return valor;
    }

    //=== Métodos para leitura de chaves individuais, caso precise futuramente
    // Lê a chave pública
    public static AsymmetricKeyParameter readPublicKey(Reader reader) throws IOException {
        try (PEMParser pem = new PEMParser(reader)) {
            SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(pem.readObject());
            return PublicKeyFactory.createKey(publicKeyInfo);
        }
    }
    
    // Lê a chave privada
    public static AsymmetricKeyParameter readPrivateKey(Reader reader) throws IOException {
        try (PEMParser pem = new PEMParser(reader)) {
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(pem.readObject());
            return PrivateKeyFactory.createKey(privateKeyInfo);
        }
    }
}
