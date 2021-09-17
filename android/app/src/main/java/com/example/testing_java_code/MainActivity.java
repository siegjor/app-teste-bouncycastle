package com.example.testing_java_code;

import android.content.res.AssetManager;

import androidx.annotation.NonNull;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.Security;
import java.util.Map;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

import static com.example.testing_java_code.VerifyEd448Signature.getPublicKeyFromPEMCertificate;
import static com.example.testing_java_code.VerifyEd448Signature.readPrivateKey;
import static com.example.testing_java_code.VerifyEd448Signature.readSignature;
import static com.example.testing_java_code.VerifyEd448Signature.verifyEd448Signature;


public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "crypto.labsec.dev/info";

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            // Recebe os argumentos passados na tela na função _verifyMessage,
                            // que no caso é o que o usuário digitou no TextField
                            final Map<String, Object> arguments = call.arguments();

                            if (call.method.equals("verifyMessage")) {
                                //TODO: testar sem essa linha
                                Security.addProvider(new BouncyCastleProvider());
                                // Guarda a mensagem passada em uma nova variável
                                String messageToBeVerified = (String) arguments.get("message");
                                // Cria um AssetManager, que permite abrir os arquivos guardados na
                                // pasta "assets"
                                AssetManager assetManager = getAssets();
                                
                                // Inicializa a variável que irá guardar o certificado
                                InputStream certIS = null;
                                try {
                                    // Guarda o certificado, no formato InputStream (IS)
                                    certIS = assetManager.open("pki-om1-cert.pem");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                // Cria um Reader, que permite a leitura de input streams
                                // É necessário um Reader pois a função que faz a leitura do
                                // certificado requer um argumento deste tipo
                                Reader certISReader = new InputStreamReader(certIS);
                                
                                InputStream sigIS = null;
                                try {
                                    sigIS = assetManager.open("pki-om1-sig.bin");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                AsymmetricKeyParameter publicKey = null;
                                byte[] signature = null;
                                try {
                                    // Extrai a chave pública do certificado, e lê a assinatura
                                    publicKey = getPublicKeyFromPEMCertificate(certISReader);
                                    signature = readSignature(sigIS);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (messageToBeVerified != null) {
                                    try {
                                        // Verifica se a assinatura é válida para a mensagem
                                        boolean isSigValid = verifyEd448Signature(publicKey, messageToBeVerified, signature);
                                        System.out.println(isSigValid);
                                        // Retorna o resultado da verificação para a tela
                                        result.success(">> The signature is: " + isSigValid + "\n>> The message was: " + messageToBeVerified);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                );
    }
}