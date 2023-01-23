package za.co.digitalcowboy.global.services.exceptions;

import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.http.OAuth2ErrorHandler;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.stereotype.Component;

@Component
public class MambuResponseOAuth2ErrorHandler extends OAuth2ErrorHandler {
    MambuResponseOAuth2ErrorHandler(OAuth2ProtectedResourceDetails resource) {
        super(resource);
    }

    public boolean hasError(ClientHttpResponse response) throws IOException {
        return false;
    }

    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
    }
}
