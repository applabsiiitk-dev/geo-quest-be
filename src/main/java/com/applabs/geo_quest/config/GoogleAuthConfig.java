package com.applabs.geo_quest.config;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

@Configuration
/**
 * Configuration class for Google OAuth authentication in GeoQuest.
 * <p>
 * Provides a singleton {@link GoogleIdTokenVerifier} bean for verifying Google
 * ID tokens.
 * Ensures efficient certificate caching and correct audience validation,
 * matching the client ID
 * used by the Flutter app. This avoids unnecessary network activity and
 * guarantees secure authentication.
 * <p>
 * Usage:
 * <ul>
 * <li>Inject {@link GoogleIdTokenVerifier} wherever Google token verification
 * is needed.</li>
 * <li>Set <code>app.google.client-id</code> in application properties to match
 * the OAuth client ID used by Flutter.</li>
 * </ul>
 *
 * @author fl4nk3r
 */
public class GoogleAuthConfig {

    /**
     * Creates a singleton {@link GoogleIdTokenVerifier} bean for verifying Google
     * ID tokens.
     * <p>
     * Internally caches Google's public key certificates and only re-fetches them
     * when the cache expires,
     * ensuring efficient and secure authentication. The client ID must match the
     * OAuth 2.0 client ID used
     * by the Flutter app, as it is validated as the audience in the token.
     *
     * @param clientId the OAuth 2.0 client ID used by the Flutter app
     * @return a singleton GoogleIdTokenVerifier configured for the specified client
     *         ID
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