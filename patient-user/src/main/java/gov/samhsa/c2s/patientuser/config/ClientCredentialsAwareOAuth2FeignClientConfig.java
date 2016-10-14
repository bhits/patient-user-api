package gov.samhsa.c2s.patientuser.config;

import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

//TODO: remove this configuration when PHR API is refactored to a service that can be called by this API without OAuth2 token
public class ClientCredentialsAwareOAuth2FeignClientConfig {

    @Bean
    public OAuth2FeignRequestInterceptor oAuth2ClientCredentialsFeignRequestInterceptor(OAuth2ClientContext oAuth2ClientContext, OAuth2ProtectedResourceDetails resource, ClientCredentialsResourceDetails clientCredentialsResourceDetails) {
        return new ClientCredentialsAwareOAuth2FeignRequestInterceptor(oAuth2ClientContext, resource, clientCredentialsResourceDetails);
    }
}