package org.weewelchie.dynamo.sensordata.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDynamoDBRepositories (basePackages = "org.weewelchie.dynamo.sensordata.repositories")
public class DynamoDBConfig {

    @Autowired
    AwsProperties awsProperties;

    @Autowired
    private ApplicationContext context;

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        com.amazonaws.auth.AWSCredentialsProvider credentialsProvider;
        String accessKey = awsProperties.getAccessKey();
        String secretKey = awsProperties.getSecretKey();

        if (accessKey != null && !accessKey.trim().isEmpty() &&
            secretKey != null && !secretKey.trim().isEmpty()) {
            credentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
        } else {
            // Fallback: If local endpoint is used, use dummy credentials for DynamoDB local.
            // Otherwise, use DefaultAWSCredentialsProviderChain for production IAM roles.
            if (awsProperties.getEndPointURL() != null && !awsProperties.getEndPointURL().trim().isEmpty()) {
                credentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials("dummyAccessKey", "dummySecretKey"));
            } else {
                credentialsProvider = new com.amazonaws.auth.DefaultAWSCredentialsProviderChain();
            }
        }

        String region = awsProperties.getRegion();
        if (region == null || region.trim().isEmpty()) {
            region = "us-east-1"; // Fallback region
        }

        if (awsProperties.getEndPointURL() != null && !awsProperties.getEndPointURL().trim().isEmpty()) {
            AwsClientBuilder.EndpointConfiguration endpointConfiguration =
                    new AwsClientBuilder.EndpointConfiguration(awsProperties.getEndPointURL(), region);
            return AmazonDynamoDBClientBuilder.standard()
                    .withCredentials(credentialsProvider)
                    .withEndpointConfiguration(endpointConfiguration)
                    .build();
        } else {
            return AmazonDynamoDBClientBuilder.standard()
                    .withCredentials(credentialsProvider)
                    .withRegion(region)
                    .build();
        }
    }

    @Bean
    public AWSCredentials amazonAWSCredentials() {
        String accessKey = awsProperties.getAccessKey();
        String secretKey = awsProperties.getSecretKey();
        if (accessKey == null || accessKey.trim().isEmpty()) {
            accessKey = "dummyAccessKey";
        }
        if (secretKey == null || secretKey.trim().isEmpty()) {
            secretKey = "dummySecretKey";
        }
        return new BasicAWSCredentials(accessKey, secretKey);
    }

}
