package org.weewelchie.dynamo.sensordata.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.weewelchie.dynamo.sensordata.model.SensorData;
import org.weewelchie.dynamo.sensordata.repositories.SensorDataRepository;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.internal.waiters.ResponseOrException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@RestController
@RequestMapping("/sensordata/")
public class SensorDataController {

    private static final String DYNAMODB_ENDPOINT = "amazon.dynamodb.endpoint";
    private static final String AWS_ACCESSKEY = "amazon.aws.accesskey";
    private static final String AWS_SECRETKEY = "amazon.aws.secretkey";

    private static final String AWS_REGION = "amazon.aws.region";

    private  static final Logger logger = LoggerFactory.getLogger(SensorDataController.class);

    @Autowired
    SensorDataRepository sensorDataRepository;
    @GetMapping(value = "/all")
    public List<SensorData> findAll()
    {
        return (List<SensorData>) sensorDataRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public Optional<SensorData> findById(@PathVariable final String id)
    {
        return sensorDataRepository.findById(id);
    }

    @GetMapping(value = "/create")
    public String create(@RequestParam(value = "id") String sensorId,
                         @RequestParam(value = "date") String date,
                         @RequestParam(value = "tempC") String tempC,
                         @RequestParam(value = "tempF") String tempF
                         )
    {
          return sensorDataRepository.save(new SensorData(sensorId,date,tempC,tempF)).toString();
    }

    @GetMapping(value="/admin/create")
    public String createSensorDataTable() throws URISyntaxException {
        Properties testProperties = loadFromFileInClasspath("application.properties")
                .filter(properties -> !isEmpty(properties.getProperty(AWS_REGION)))
                .filter(properties -> !isEmpty(properties.getProperty(AWS_ACCESSKEY)))
                .filter(properties -> !isEmpty(properties.getProperty(DYNAMODB_ENDPOINT)))
                .filter(properties -> !isEmpty(properties.getProperty(AWS_SECRETKEY))).orElseThrow(() -> new RuntimeException("Unable to get all of the required test property values"));

        String amazonAWSRegion = testProperties.getProperty(AWS_REGION);
        String amazonAWSAccessKey = testProperties.getProperty(AWS_ACCESSKEY);
        String amazonAWSSecretKey = testProperties.getProperty(AWS_SECRETKEY);
        String amazonAWSEndPointURL = testProperties.getProperty(DYNAMODB_ENDPOINT);
        AwsCredentialsProvider creds = StaticCredentialsProvider.create(AwsBasicCredentials.create(amazonAWSAccessKey, amazonAWSSecretKey));

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(
                        // Configure an instance of the standard client.
                        DynamoDbClient.builder()
                                .region(Region.of(amazonAWSRegion))
                                .endpointOverride(new URI(amazonAWSEndPointURL))
                                .credentialsProvider(creds)
                                .build())
                .build();


        DynamoDbClient dbClient = DynamoDbClient.builder()
                .region(Region.of(amazonAWSRegion))
                .endpointOverride(new URI(amazonAWSEndPointURL) )
                .credentialsProvider(creds)
                .build();
        DynamoDbTable<SensorData> sensorDataTable =
                enhancedClient.table("SensorData", TableSchema.fromBean(SensorData.class));
        return createSensorDataTable(sensorDataTable,dbClient);

    }

    public static String createSensorDataTable(DynamoDbTable<SensorData> sensorData2DynamoDbTable, DynamoDbClient dynamoDbClient) {
        // Create the DynamoDB table by using the 'customerDynamoDbTable' DynamoDbTable instance.
        sensorData2DynamoDbTable.createTable(builder -> builder
                .provisionedThroughput(b -> b
                        .readCapacityUnits(10L)
                        .writeCapacityUnits(10L)
                        .build())
        );
        // The 'dynamoDbClient' instance that's passed to the builder for the DynamoDbWaiter is the same instance
        // that was passed to the builder of the DynamoDbEnhancedClient instance used to create the 'customerDynamoDbTable'.
        // This means that the same Region that was configured on the standard 'dynamoDbClient' instance is used for all service clients.
        try (DynamoDbWaiter waiter = DynamoDbWaiter.builder().client(dynamoDbClient).build()) { // DynamoDbWaiter is Autocloseable
            ResponseOrException<DescribeTableResponse> response = waiter
                    .waitUntilTableExists(builder -> builder.tableName("SensorData").build())
                    .matched();
            DescribeTableResponse tableDescription = response.response().orElseThrow(
                    () -> new RuntimeException("SensorData table was not created."));
            // The actual error can be inspected in response.exception()

            logger.info("SensorData table was created.");
            return "SensorData table was created";
        }
    }

    private static Optional<Properties> loadFromFileInClasspath(String fileName) {
        InputStream stream = null;
        try {
            Properties config = new Properties();
            Path configLocation = Paths.get(ClassLoader.getSystemResource(fileName).toURI());
            stream = Files.newInputStream(configLocation);
            config.load(stream);
            return Optional.of(config);
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static boolean isEmpty(String inputString) {
        return inputString == null || "".equals(inputString);
    }

}