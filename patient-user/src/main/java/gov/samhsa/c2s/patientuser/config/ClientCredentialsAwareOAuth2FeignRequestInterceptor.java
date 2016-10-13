package gov.samhsa.c2s.patientuser.config;

import feign.RequestTemplate;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.implicit.ImplicitAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class ClientCredentialsAwareOAuth2FeignRequestInterceptor extends OAuth2FeignRequestInterceptor {
    private ThreadLocal<Boolean> enableClientCredentials = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return Boolean.FALSE;
        }
    };
    private OAuth2ClientContext oAuth2ClientContext;
    private OAuth2ProtectedResourceDetails clientCredentialsResource;
    private AccessTokenProvider accessTokenProvider = new AccessTokenProviderChain(Arrays
            .<AccessTokenProvider>asList(new AuthorizationCodeAccessTokenProvider(),
                    new ImplicitAccessTokenProvider(),
                    new ResourceOwnerPasswordAccessTokenProvider(),
                    new ClientCredentialsAccessTokenProvider()));

    public ClientCredentialsAwareOAuth2FeignRequestInterceptor(OAuth2ClientContext oAuth2ClientContext, OAuth2ProtectedResourceDetails defaultResource, OAuth2ProtectedResourceDetails clientCredentialsResource) {
        super(oAuth2ClientContext, defaultResource);
        this.oAuth2ClientContext = oAuth2ClientContext;
        this.clientCredentialsResource = clientCredentialsResource;
    }

    public ClientCredentialsAwareOAuth2FeignRequestInterceptor(OAuth2ClientContext oAuth2ClientContext, OAuth2ProtectedResourceDetails resource) {
        super(oAuth2ClientContext, resource);
    }

    public ClientCredentialsAwareOAuth2FeignRequestInterceptor(OAuth2ClientContext oAuth2ClientContext, OAuth2ProtectedResourceDetails resource, String tokenType, String header) {
        super(oAuth2ClientContext, resource, tokenType, header);
    }

    @Override
    public void apply(RequestTemplate template) {
        if (template.headers().containsKey(TokenRelayTypeHeader.HEADER_KEY)) {
            template.headers().entrySet().stream()
                    .filter(entry -> TokenRelayTypeHeader.HEADER_KEY.equals(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .flatMap(Collection::stream)
                    .map(TokenRelayTypeHeader.CLIENT_CREDENTIALS::equals)
                    .filter(Boolean.TRUE::equals)
                    .findAny().ifPresent(enableClientCredentials::set);
            final Map<String, Collection<String>> filteredHeaders = template.headers().entrySet().stream()
                    .filter(entry -> !TokenRelayTypeHeader.HEADER_KEY.equals(entry.getKey()))
                    .collect(toMap(entry -> entry.getKey(), entry -> entry.getValue()));
            template.headers(null);
            template.headers(filteredHeaders);
        }
        super.apply(template);
    }

    @Override
    protected OAuth2AccessToken acquireAccessToken() throws UserRedirectRequiredException {
        if (enableClientCredentials.get()) {
            AccessTokenRequest tokenRequest = oAuth2ClientContext.getAccessTokenRequest();
            if (tokenRequest == null) {
                throw new AccessTokenRequiredException(
                        "Cannot find valid context on request for resource '"
                                + clientCredentialsResource.getId() + "'.",
                        clientCredentialsResource);
            }
            String stateKey = tokenRequest.getStateKey();
            if (stateKey != null) {
                tokenRequest.setPreservedState(
                        oAuth2ClientContext.removePreservedState(stateKey));
            }
            OAuth2AccessToken existingToken = oAuth2ClientContext.getAccessToken();
            if (existingToken != null) {
                oAuth2ClientContext.setAccessToken(existingToken);
            }
            OAuth2AccessToken obtainableAccessToken;
            obtainableAccessToken = accessTokenProvider.obtainAccessToken(clientCredentialsResource,
                    tokenRequest);
            if (obtainableAccessToken == null || obtainableAccessToken.getValue() == null) {
                throw new IllegalStateException(
                        " Access token provider returned a null token, which is illegal according to the contract.");
            }
            oAuth2ClientContext.setAccessToken(obtainableAccessToken);
            return obtainableAccessToken;
        } else {
            return super.acquireAccessToken();
        }
    }
}