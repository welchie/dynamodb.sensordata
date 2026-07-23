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
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.regions.Region;
import java.net.URI;

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
            if (awsProperties.getEndpointUrl() != null && !awsProperties.getEndpointUrl().trim().isEmpty()) {
                credentialsProvider = new AWSStaticCredentialsProvider(new BasicAWSCredentials("dummyAccessKey", "dummySecretKey"));
            } else {
                credentialsProvider = new com.amazonaws.auth.DefaultAWSCredentialsProviderChain();
            }
        }

        String region = awsProperties.getRegion();
        if (region == null || region.trim().isEmpty()) {
            region = "us-east-1"; // Fallback region
        }

        if (awsProperties.getEndpointUrl() != null && !awsProperties.getEndpointUrl().trim().isEmpty()) {
            AwsClientBuilder.EndpointConfiguration endpointConfiguration =
                    new AwsClientBuilder.EndpointConfiguration(awsProperties.getEndpointUrl(), region);
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

    @Bean
    public DynamoDbClient dynamoDbClient() {
        AwsCredentialsProvider credentialsProvider;
        String accessKey = awsProperties.getAccessKey();
        String secretKey = awsProperties.getSecretKey();

        if (accessKey != null && !accessKey.trim().isEmpty() &&
            secretKey != null && !secretKey.trim().isEmpty()) {
            credentialsProvider = StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
        } else {
            if (awsProperties.getEndpointUrl() != null && !awsProperties.getEndpointUrl().trim().isEmpty()) {
                credentialsProvider = StaticCredentialsProvider.create(AwsBasicCredentials.create("dummyAccessKey", "dummySecretKey"));
            } else {
                credentialsProvider = software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider.create();
            }
        }

        String regionStr = awsProperties.getRegion();
        if (regionStr == null || regionStr.trim().isEmpty()) {
            regionStr = "us-east-1";
        }
        Region region = Region.of(regionStr);

        if (awsProperties.getEndpointUrl() != null && !awsProperties.getEndpointUrl().trim().isEmpty()) {
            try {
                return DynamoDbClient.builder()
                        .credentialsProvider(credentialsProvider)
                        .endpointOverride(new URI(awsProperties.getEndpointUrl()))
                        .region(region)
                        .build();
            } catch (Exception e) {
                throw new RuntimeException("Failed to construct DynamoDbClient with local endpoint override", e);
            }
        } else {
            return DynamoDbClient.builder()
                    .credentialsProvider(credentialsProvider)
                    .region(region)
                    .build();
        }
    }

}
