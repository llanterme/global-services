package za.co.digitalcowboy.global.services.controller;


import com.amazonaws.services.sqs.model.Message;
import io.awspring.cloud.messaging.listener.SqsMessageDeletionPolicy;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import za.co.digitalcowboy.global.services.config.AWSConfig;
import za.co.digitalcowboy.global.services.domain.*;
import za.co.digitalcowboy.global.services.service.ExternalService;

import java.io.IOException;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
@Slf4j

public class RestController {

    private ExternalService externalService;

    private AWSConfig awsConfig;

    private int reprocessMaxCount = 2;

    @Value("${cloud.aws.sqs.shipping-dlq.url}")
    private String shippingDlq;

    @Autowired
    RestController (ExternalService externalService, AWSConfig awsConfig){
        this.externalService = externalService;
        this.awsConfig = awsConfig;
    }

    @PostMapping(value = "/receive-shipping-sqs-message", consumes = MediaType.APPLICATION_JSON_VALUE)
    @SqsListener(value = "${cloud.aws.sqs.shipping-queue.url}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    @ResponseStatus(HttpStatus.CREATED)
    public void receiveShippingMessage(@Headers Map<String, String> headers, @Header("ApproximateReceiveCount") String approximateReceiveCount, @Payload Message message) throws Exception {

        try {
            EnhancedProfile enhancedProfile =  parseMessage(message);

            EnhancedProfile profile = parseSNSMessage(message, EnhancedProfile.class);
            System.out.printf(profile.getEmail_address());



        } catch (RuntimeException ex) {
            handleException(approximateReceiveCount, message, ex);
        }


    }

    @PostMapping(value = "/receive-shipping-dlq-sqs-message", consumes = MediaType.APPLICATION_JSON_VALUE)
    @SqsListener(value = "${cloud.aws.sqs.shipping-dlq.url}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    @ResponseStatus(HttpStatus.CREATED)
    public void receiveShippingMessageDLQ(@Headers Map<String, String> headers, @Header("ApproximateReceiveCount") String approximateReceiveCount, @Payload Message message) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(message.getBody());
        JsonNode messageNode = jsonNode.get("Message");
        String messageBody = messageNode.asText();
        System.out.printf(messageBody);

    }

    @RequestMapping(path = "/health", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Version> getHealth() {

        Version version = new Version();
        version.setService("Global Service");
        version.setMessage("Healthy");
        version.setServiceVersion("1.0.0");

        return ResponseEntity.status(HttpStatus.OK).body(version);
    }

    @RequestMapping(path = "/bored-fact", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BoredResponse> getBorderFact() {

        return ResponseEntity.status(HttpStatus.OK).body(externalService.getRandomBoredFact());
    }

    @PostMapping(value = "/register-user")
    public ResponseEntity registerUser( @RequestBody User user) {

        log.info("start register user request:", user);
        try {
            user.setUserId(1092831231);
            return ResponseEntity.status(HttpStatus.OK).body(user);

        } catch (Exception serviceException) {
            return buildKnoxServiceExceptionResponse();
        }

    }

    @PostMapping(value = "/register-user-error")
    public ResponseEntity registerUserError( @RequestBody User user) {

        try {
            throw new Exception();

        } catch (Exception serviceException) {
            return buildKnoxServiceExceptionResponse();
        }

    }

    private ResponseEntity<?> buildKnoxServiceExceptionResponse() {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setResponseMessage("User already registered");
        errorResponse.setResponseCode(011);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    private void handleException(String approximateReceiveCount, Message message, RuntimeException ex) {
        log.error("Error while trying to process message. Message approximateReceiveCount  {} ",
                approximateReceiveCount, ex.getLocalizedMessage());
        int approximateReceiveCountInt = Integer.parseInt(approximateReceiveCount);

//        if (approximateReceiveCountInt <= reprocessMaxCount) {
//            log.error("Returning message to a queue...");
//            throw ex;
//        }
        log.error("All {} tries are failed. Redirecting message to DLQ...", approximateReceiveCountInt);
        awsConfig.getAmazonSQSAsyncClient().sendMessage(shippingDlq,message.getBody());
    }

    public <T> T parseSNSMessage(Message sqsMessage, Class<T> pojoClass) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(sqsMessage.getBody());
            JsonNode messageNode = jsonNode.get("Message");
            String message = messageNode.asText();

            return mapper.readValue(message, pojoClass);


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


    }

    public EnhancedProfile parseMessage(Message sqsMessage) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(sqsMessage.getBody());
            JsonNode messageNode = jsonNode.get("Message");
            String message = messageNode.asText();
            EnhancedProfile enhancedProfile = mapper.readValue(message, EnhancedProfile.class);
            return enhancedProfile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



}
