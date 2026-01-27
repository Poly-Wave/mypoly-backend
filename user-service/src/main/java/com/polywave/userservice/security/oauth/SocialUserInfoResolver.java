package com.polywave.userservice.security.oauth;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SocialUserInfoResolver {

    private final Map<String, SocialUserInfoProvider> providerMap;

    public SocialUserInfoResolver(List<SocialUserInfoProvider> providers) {
        this.providerMap = providers.stream().collect(Collectors.toUnmodifiableMap(
                SocialUserInfoProvider::provider,
                Function.identity(),
                (a, b) -> {
                    throw new IllegalStateException("Duplicate SocialUserInfoProvider for provider=" + a.provider());
                }
        ));
    }

    public SocialUserInfo resolve(String provider, OAuth2User oAuth2User) {
        SocialUserInfoProvider p = providerMap.get(provider);
        if (p == null) {
            throw new UnsupportedOAuth2ProviderException(provider);
        }
        return p.extract(oAuth2User);
    }
}
