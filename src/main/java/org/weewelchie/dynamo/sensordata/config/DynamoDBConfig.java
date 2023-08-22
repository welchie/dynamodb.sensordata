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
        if (!awsProperties.getEndPointURL().isEmpty() || !awsProperties.getEndPointURL().equals(""))
        {
            AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(awsProperties.getEndPointURL() ,awsProperties.getRegion()) ;
            return AmazonDynamoDBClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider
                            (new BasicAWSCredentials(awsProperties.getAccessKey(),awsProperties.getSecretKey())))
                    .withEndpointConfiguration(endpointConfiguration)
                    .build();
        }
        else
        {
            return AmazonDynamoDBClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider
                            (new BasicAWSCredentials(awsProperties.getAccessKey(),awsProperties.getSecretKey())))
                    .withRegion(awsProperties.getRegion())
                    .build();
        }


    }

    @Bean
    public AWSCredentials amazonAWSCredentials() {
        return new BasicAWSCredentials(
                awsProperties.getAccessKey(), awsProperties.getSecretKey());
    }

}
