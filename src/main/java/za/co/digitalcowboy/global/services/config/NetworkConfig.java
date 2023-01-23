package za.co.digitalcowboy.global.services.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import za.co.digitalcowboy.global.services.exceptions.MambuResponseOAuth2ErrorHandler;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Configuration
public class NetworkConfig {

    @Value("${app.connector.api-gateway.schema}")
    String scheme;
    @Value("${app.connector.api-gateway.host}")
    String host;
    @Value("${app.connector.api-gateway.port}")
    int port;

    public static final String URI_BUILDER = "ServiceUriBuilder";
    public static final String REST_TEMPLATE = "RestTemplate";

    @Autowired
    private MambuResponseOAuth2ErrorHandler mambuResponseOAuth2ErrorHandler;

    @Bean
    @Qualifier(URI_BUILDER)
    public URIBuilder uriBuilder() {
        return new URIBuilder().setHost(host).setPort(port).setScheme(scheme);
    }


    @Bean
    @Qualifier(REST_TEMPLATE)
    public OAuth2RestTemplate getRestTemplate(OAuth2ProtectedResourceDetails details,
                                                     OAuth2ClientInterceptor oauth2ClientInterceptor) {
        OAuth2RestTemplate oAuth2RestTemplate = getOAuth2RestTemplate(host, details, 6000);
        oAuth2RestTemplate.setRetryBadAccessTokens(true);
        oAuth2RestTemplate.setRequestFactory(getHttpComponentsClientHttpRequestFactory(6000));
        oAuth2RestTemplate.setErrorHandler(mambuResponseOAuth2ErrorHandler);
        oAuth2RestTemplate.setInterceptors(Collections.singletonList(oauth2ClientInterceptor));
        return oAuth2RestTemplate;
    }

    private OAuth2RestTemplate getOAuth2RestTemplate(String host, OAuth2ProtectedResourceDetails details, int timeout) {
        OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(details);
        ClientCredentialsAccessTokenProvider clientCredentialsAccessTokenProvider = new ClientCredentialsAccessTokenProvider();
        clientCredentialsAccessTokenProvider.setRequestFactory(getHttpComponentsClientHttpRequestFactory(timeout));
        oAuth2RestTemplate.setAccessTokenProvider(clientCredentialsAccessTokenProvider);
        return oAuth2RestTemplate;
    }

    private HttpComponentsClientHttpRequestFactory getHttpComponentsClientHttpRequestFactory(int readTimeout) {
        PoolingHttpClientConnectionManager poolingConnManager
                = new PoolingHttpClientConnectionManager();
        poolingConnManager.setMaxTotal(100);
        poolingConnManager.setDefaultMaxPerRoute(100);
        HttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                .setConnectionManager(poolingConnManager)
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        requestFactory.setConnectionRequestTimeout(6000);
        requestFactory.setReadTimeout(readTimeout);

        return requestFactory;
    }


    @Bean
    public OAuth2ClientInterceptor oauth2ClientInterceptorV2(@Value("payment-stack.auth.eu-west-2.amazoncognito.com") String host,
                                                             OAuth2ProtectedResourceDetails details) {
        return new OAuth2ClientInterceptor(getOAuth2RestTemplate(host, details, 6000));
    }
    private static class OAuth2ClientInterceptor implements ClientHttpRequestInterceptor {

        private final OAuth2RestTemplate restTemplate;

        OAuth2ClientInterceptor(OAuth2RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
        }

        @Override
        public ClientHttpResponse intercept(org.springframework.http.HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
            HttpRequest requestWrapper = addAuthorizationHeader(httpRequest);
            return clientHttpRequestExecution.execute(requestWrapper, bytes);
        }

        private HttpRequest addAuthorizationHeader(HttpRequest request) {
            OAuth2AccessToken accessToken = restTemplate.getAccessToken();
            HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
            requestWrapper.getHeaders().set(HttpHeaders.AUTHORIZATION, String.join(" ", accessToken.getTokenType(), accessToken.getValue()));
            return requestWrapper;
        }
    }
}

