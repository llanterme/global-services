package za.co.digitalcowboy.global.services.config;


import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class AWSConfig {



    @Getter
    private AmazonSQSAsync amazonSQSAsyncClient;

    @Setter
    @Value("${cloud.aws.region.static:eu-west-2}")
    private String region;


    @Bean
    public void setAmazonSQSAsyncClient() {
        amazonSQSAsyncClient = AmazonSQSAsyncClientBuilder.standard()
                .withRegion(region)
                .build();
    }



}