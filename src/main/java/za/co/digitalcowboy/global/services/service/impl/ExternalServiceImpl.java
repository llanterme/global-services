package za.co.digitalcowboy.global.services.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import za.co.digitalcowboy.global.services.domain.BoredResponse;
import za.co.digitalcowboy.global.services.service.ExternalService;
import za.co.digitalcowboy.global.services.utils.Utils;

import static za.co.digitalcowboy.global.services.config.NetworkConfig.REST_TEMPLATE;
import static za.co.digitalcowboy.global.services.config.NetworkConfig.URI_BUILDER;
import static za.co.digitalcowboy.global.services.utils.Utils.getHeaders;


@Slf4j
@Component
public class ExternalServiceImpl implements ExternalService {

    private RestTemplate restTemplate;
    private final URIBuilder uriBuilder;

    @Value("${app.connector.api-gateway.context}")
    private String apiContext;

    @Value("${app.connector.api-gateway.operations.boredFact}")
    private String boredFactPath;

    @Autowired
    public ExternalServiceImpl(@Qualifier(URI_BUILDER) URIBuilder uriBuilder,
                             @Qualifier(REST_TEMPLATE) RestTemplate restTemplate) {
        this.uriBuilder = uriBuilder;
        this.restTemplate = restTemplate;
    }

    @Override
    public BoredResponse getRandomBoredFact() {

        try {
            log.info("method: get random bored fact");

            String getClientUri = Utils.buildUri(uriBuilder, apiContext, boredFactPath);
            log.info("getClientUri {}", getClientUri);

            HttpEntity<Object> entity = new HttpEntity<>(getHeaders());
            ResponseEntity<String> responseEntity = restTemplate.exchange(getClientUri, HttpMethod.GET, entity,
                    String.class);

            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                log.error("Error getting client details");
                throw new Exception("Error getting client details");
            }

            BoredResponse boredResponse = Utils.fromJson(responseEntity.getBody(), BoredResponse.class);
            log.info("method: get random bored fact - success - {}", responseEntity);

            return boredResponse;

        } catch (Exception e) {
            log.error("method: getClientFromMambu - Error getting client details", e);
        }
        return null;

    }
}
