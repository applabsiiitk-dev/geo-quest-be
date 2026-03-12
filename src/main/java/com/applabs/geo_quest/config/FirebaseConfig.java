package com.applabs.geo_quest.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

/**
 * Configuration class for Firebase Admin SDK.
 * <p>
 * Builds the service account JSON from individual environment variables
 * and initializes Firebase Admin SDK for verifying Firebase ID tokens.
 *
 * @author fl4nk3r
 */
@Configuration
public class FirebaseConfig {

    /** Firebase project ID — pre-filled as geo-quest-applabs */
    @Value("${firebase.project.id}")
    private String projectId;

    /** Service account client email — from Firebase Console service account JSON */
    @Value("${firebase.client.email}")
    private String clientEmail;

    /** Private key ID — from Firebase Console service account JSON */
    @Value("${firebase.private.key.id}")
    private String privateKeyId;

    /** RSA private key — from Firebase Console service account JSON (keep newlines as \\n) */
    @Value("${firebase.private.key}")
    private String privateKey;

    /** Service account client ID — from Firebase Console service account JSON */
    @Value("${firebase.client.id}")
    private String clientId;

    /** Client x509 cert URL — from Firebase Console service account JSON */
    @Value("${firebase.client.cert.url}")
    private String clientCertUrl;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                // Build service account JSON from individual environment variables
                String serviceAccountJson = String.format(
                    "{" +
                    "\"type\": \"service_account\"," +
                    "\"project_id\": \"%s\"," +
                    "\"private_key_id\": \"%s\"," +
                    "\"private_key\": \"%s\"," +
                    "\"client_email\": \"%s\"," +
                    "\"client_id\": \"%s\"," +
                    "\"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\"," +
                    "\"token_uri\": \"https://oauth2.googleapis.com/token\"," +
                    "\"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\"," +
                    "\"client_x509_cert_url\": \"%s\"," +
                    "\"universe_domain\": \"googleapis.com\"" +
                    "}",
                    projectId,
                    privateKeyId,
                    // Restore actual newlines in the PEM key (env vars store \n as literal \n)
                    privateKey.replace("\\n", "\n"),
                    clientEmail,
                    clientId,
                    clientCertUrl
                );

                InputStream serviceAccountStream = new ByteArrayInputStream(
                    serviceAccountJson.getBytes(StandardCharsets.UTF_8)
                );

                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                    .build();

                FirebaseApp.initializeApp(options);
                System.out.println("Firebase Admin SDK initialized for project: " + projectId);
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize Firebase: " + e.getMessage());
            throw new RuntimeException("Failed to initialize Firebase Admin SDK", e);
        }
    }
}
