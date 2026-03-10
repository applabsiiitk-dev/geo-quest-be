package com.applabs.geo_quest.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class GoogleAuthConfig {

    /**
     * Singleton GoogleIdTokenVerifier bean.
     *
     * Internally caches Google's public key certificates and only
     * re-fetches them when the cache expires — so this is both
     * correct and efficient. Creating a new verifier per request
     * (as was done inline in AuthController) would cause unnecessary
     * network activity on every login.
     *
     * The client ID must match exactly what Flutter sends as the
     * audience in the Google ID token — use the same OAuth 2.0
     * client ID from Google Cloud Console that your Flutter app uses.
     */
    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier(
            @Value("${app.google.client-id}") String clientId) {
        return new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }
}