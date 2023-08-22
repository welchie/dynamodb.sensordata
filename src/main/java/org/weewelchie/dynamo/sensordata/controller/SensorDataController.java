package org.weewelchie.dynamo.sensordata.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.weewelchie.dynamo.sensordata.config.AwsProperties;
import org.weewelchie.dynamo.sensordata.config.DynamoDBConfig;
import org.weewelchie.dynamo.sensordata.exception.AwsPropertiesException;
import org.weewelchie.dynamo.sensordata.exception.SensorDataException;
import org.weewelchie.dynamo.sensordata.model.SensorData;
import org.weewelchie.dynamo.sensordata.model.SensorDataId;
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
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@RestController
@RequestMapping("/sensordata/")
public class SensorDataController {

    private  static final Logger logger = LoggerFactory.getLogger(SensorDataController.class);

    @Autowired
    SensorDataRepository sensorDataRepository;

    @Autowired
    AwsProperties awsProperties;

    @Autowired
    DynamoDBConfig dynamoDBConfig;

    private static final String TABLE_NAME = "SensorData";
    private static final String ERROR_TITLE = "errors";

    @GetMapping(value = "/all")
    public ResponseEntity<?> findAll()
    {
        try {
            logger.info("Getting all data....");
            Map<String, List<SensorData>> response = new HashMap<>(1);
            List<SensorData> result = (List<SensorData>) sensorDataRepository.findAll();
            response.put(TABLE_NAME,result);
            return new ResponseEntity<>(response, HttpStatus.OK);

        }
        catch(Exception e)
        {
            logger.error(e.getMessage());
            Map<String, List<String>> response = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            response.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{id}/{date}")
    public ResponseEntity<?> findByIdAndDate(@PathVariable final String id,
                                         @PathVariable final String date)
    {
        try {
            logger.info("Finding data by id: " + id + " and date: " + date);
            SensorDataId sensorDataId = new SensorDataId(id,date);

            Map<String, Optional<SensorData>> response = new HashMap<>(1);
            Optional<SensorData> result = (Optional<SensorData>) sensorDataRepository.findById(sensorDataId);
            if (!result.stream().findAny().isPresent())
            {
                return new ResponseEntity<>("Not found",HttpStatus.NOT_FOUND);
            }
            response.put(TABLE_NAME, result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            Map<String, List<String>> response = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            response.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> findById(@PathVariable final String id)
    {
        try {
            logger.info("Find by id: " + id);

            Map<String, List<SensorData>> response = new HashMap<>(1);
            List<SensorData> result = sensorDataRepository.findById(id);
            if (result.isEmpty())
            {
                return new ResponseEntity<>("Not found",HttpStatus.NOT_FOUND);
            }
            response.put(TABLE_NAME, result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            Map<String, List<String>> response = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            response.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/find")
    public ResponseEntity<?> findByDate(@RequestParam(value="date") String date)    {

        try {
            logger.info("Find by date: " + date);

            Map<String, List<SensorData>> response = new HashMap<>(1);
            List<SensorData> result = sensorDataRepository.findByDate(date);
            if (result.isEmpty())
            {
                return new ResponseEntity<>("Not found",HttpStatus.NOT_FOUND);
            }
            response.put(TABLE_NAME, result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            Map<String, List<String>> response = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            response.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/findbetween")
    public ResponseEntity<?> findByDates(@RequestParam(value="startDate") String startDate,
                                        @RequestParam(value="endDate") String endDate)    {

        try {
            logger.info("Find between start date: " + startDate + " and end date: " + endDate);

            Map<String, List<SensorData>> response = new HashMap<>(1);
            List<SensorData> result = sensorDataRepository.findByDateBetween(startDate,endDate);
            if (result.isEmpty())
            {
                return new ResponseEntity<>("Not found",HttpStatus.NOT_FOUND);
            }
            response.put(TABLE_NAME, result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            Map<String, List<String>> response = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            response.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/create")
    public ResponseEntity<?> create(@RequestParam(value = "id") String sensorId,
                         @RequestParam(value = "date") String date,
                         @RequestParam(value = "tempC") String tempC,
                         @RequestParam(value = "tempF") String tempF
                         )
    {

        try {
            SensorData sd = new SensorData(sensorId,date,tempC,tempF);
            logger.info("Creating new record: " + sd);

            Map<String, SensorData> response = new HashMap<>(1);
            SensorData result = sensorDataRepository.save(sd);

            response.put(TABLE_NAME, result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            Map<String, List<String>> response = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            response.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value="/admin/create")
    public ResponseEntity<?> createSensorDataTable() throws URISyntaxException, AwsPropertiesException {
        if (    isEmpty(awsProperties.getRegion()) ||
                isEmpty(awsProperties.getAccessKey()) ||
                isEmpty(awsProperties.getSecretKey()))
        {
            throw new AwsPropertiesException("Unable to get AWS Properties");
        }
        logger.info("Creating SensorData table in DynamoDB...");
        AwsCredentialsProvider creds = StaticCredentialsProvider.create(AwsBasicCredentials.create(awsProperties.getAccessKey(), awsProperties.getSecretKey()));

        DynamoDbClient dbClient = null;
        if (!awsProperties.getEndPointURL().equals(""))
        {
            dbClient = DynamoDbClient.builder()
                    .region(Region.of(awsProperties.getRegion()))
                    .endpointOverride(new URI(awsProperties.getEndPointURL()) )
                    .credentialsProvider(creds)
                    .build();
        }
        else
        {
            dbClient = DynamoDbClient.builder()
                    .region(Region.of(awsProperties.getRegion()))
                    .credentialsProvider(creds)
                    .build();
        }

        DynamoDbEnhancedClient enhancedClient =
                DynamoDbEnhancedClient.builder()
                        .dynamoDbClient(dbClient)
                        .build();

        DynamoDbTable<SensorData> sensorDataTable =
                enhancedClient.table(TABLE_NAME, TableSchema.fromBean(SensorData.class));

        try
        {
            String responseText =createSensorDataTable(sensorDataTable,dbClient);
            Map<String, List<String>> response = new HashMap<String, List<String>>(1);
            List<String> result = new ArrayList<String>();
            result.add(responseText);
            response.put("result",result);
            return new ResponseEntity<Map<String, List<String>>>(response, HttpStatus.OK);
        }
        catch (SensorDataException e)
        {
            logger.error(e.getMessage());
            Map<String, List<String>> response = new HashMap<String, List<String>>(1);
            List<String> errors = new ArrayList<String>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            response.put("errors", errors);
            return new ResponseEntity<Map<String, List<String>>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private String createSensorDataTable(DynamoDbTable<SensorData> sensorDataDynamoDbTable, DynamoDbClient dynamoDbClient) throws SensorDataException{
        // Create the DynamoDB table by using the 'customerDynamoDbTable' DynamoDbTable instance.
        try{
                sensorDataDynamoDbTable.createTable(builder -> builder
                        .provisionedThroughput(b -> b
                                .readCapacityUnits(10L)
                                .writeCapacityUnits(10L)
                                .build())
                );
                // The 'dynamoDbClient' instance that's passed to the builder for the DynamoDbWaiter is the same instance
                // that was passed to the builder of the DynamoDbEnhancedClient instance used to create the 'customerDynamoDbTable'.
                // This means that the same Region that was configured on the standard 'dynamoDbClient' instance is used for all service clients.
                try (DynamoDbWaiter waiter = DynamoDbWaiter.builder().client((DynamoDbClient) dynamoDbClient).build()) { // DynamoDbWaiter is Autocloseable
                    ResponseOrException<DescribeTableResponse> response = waiter
                            .waitUntilTableExists(builder -> builder.tableName("SensorData").build())
                            .matched();
                    DescribeTableResponse tableDescription = response.response().orElseThrow(
                            () -> new RuntimeException("SensorData table was not created."));
                    // The actual error can be inspected in response.exception()
                }


        } catch (ResourceInUseException e) {
            throw new SensorDataException(e.getMessage());

        }

        logger.info("SensorData table was created.");
        return "SensorData table was created";
    }
    private boolean isEmpty(String inputString) {
        return inputString == null || "".equals(inputString);
    }

}
