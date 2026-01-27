package com.polywave.userservice.security.oauth;

public class UnsupportedOAuth2ProviderException extends RuntimeException {

    private final String provider;

    public UnsupportedOAuth2ProviderException(String provider) {
        super("Unsupported provider: " + provider);
        this.provider = provider;
    }

    public String getProvider() {
        return provider;
    }
}
