package org.weewelchie.dynamo.sensordata.controller;


import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
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
import org.weewelchie.dynamo.sensordata.service.SensorDataService;
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
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
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
    SensorDataService sensorDataService;

    @Autowired
    AwsProperties awsProperties;

    @Autowired
    DynamoDBConfig dynamoDBConfig;

    private static final String TABLE_NAME = "SensorData";
    private static final String ERROR_TITLE = "errors";

    private static final String RESULT_TITLE = "result";

    private static final String NOT_FOUND = "Not Found";

    @GetMapping(value = "/all")
    public ResponseEntity<Object> findAll()
    {
        try {
            logger.info("Getting all data....");
            Map<String, List<SensorData>> response = new HashMap<>(1);
            List<SensorData> result = sensorDataService.findAll();
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
    public ResponseEntity<Object> findByIdAndDate(@PathVariable final String id,
                                         @PathVariable final String date)
    {
        try {
            logger.info("Finding data by id: {} and date: {} ", id, date);
            SensorDataId sensorDataId = new SensorDataId(id,date);

            Map<String, Optional<SensorData>> response = new HashMap<>(1);
            Optional<SensorData> result = sensorDataService.findBySensorDataID(sensorDataId);
            if (!result.stream().findAny().isPresent())
            {
                return new ResponseEntity<>(NOT_FOUND,HttpStatus.NOT_FOUND);
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
    public ResponseEntity<Object> findById(@PathVariable final String id)
    {
        try {
            logger.info("Find by id: {}" , id);

            Map<String, List<SensorData>> response = new HashMap<>(1);
            List<SensorData> result = sensorDataService.findById(id);
            if (result.isEmpty())
            {
                return new ResponseEntity<>(NOT_FOUND,HttpStatus.NOT_FOUND);
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

    @GetMapping(value = "/find/{name}")
    public ResponseEntity<Object> findByName(@PathVariable String name)    {

        try {
            logger.info("Find by name: {}", name);

            Map<String, List<SensorData>> response = new HashMap<>(1);
            List<SensorData> result = sensorDataService.findByName(name);
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

    @GetMapping(value = "/findbynameanddatebetween")
    public ResponseEntity<Object> findByNameBetween(
            @RequestParam(value="name") String name,
            @RequestParam(value="startdate") String startdate,
            @RequestParam(value="enddate") String enddate)    {

        try {
            logger.info("Find by name between: {} : {}:{}", name,startdate,enddate);

            Map<String, List<SensorData>> response = new HashMap<>(1);
            List<SensorData> result = sensorDataService.findByNameAndDateBetween(name,startdate,enddate);
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

    @GetMapping(value = "/findbynameanddate")
    public ResponseEntity<Object> findByNameBetween(
            @RequestParam(value="name") String name,
            @RequestParam(value="date") String date)    {

        try {
            logger.info("Find by name: {} and date: {}", name,date);

            Map<String, List<SensorData>> response = new HashMap<>(1);
            List<SensorData> result = sensorDataService.findByNameAndDate(name,date);
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
    public ResponseEntity<Object> findByDates(@RequestParam(value="startDate") String startDate,
                                        @RequestParam(value="endDate") String endDate)    {

        try {
            logger.info("Find between start date: {} and end date {} ",startDate,endDate);

            Map<String, List<SensorData>> response = new HashMap<>(1);
            List<SensorData> result = sensorDataService.findBetweenDates(startDate,endDate);
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
    public ResponseEntity<Object> create(@RequestParam(value = "id") String sensorId,
                         @RequestParam(value = "name") String name,
                         @RequestParam(value = "date") String date,
                         @RequestParam(value = "tempC") String tempC,
                         @RequestParam(value = "tempF") String tempF
                         )
    {

        try {
            SensorData sd = new SensorData(sensorId,name, date,tempC,tempF);
            logger.info("Creating new record: {}" , sd);

            Map<String, SensorData> response = new HashMap<>(1);
            SensorData result = sensorDataService.createRecord(sd);

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

    @DeleteMapping(value = "/delete/{id}/{date}")
    public ResponseEntity<Object> deleteByIDAndDate(@PathVariable final String id,
                                                    @PathVariable final String date)    {

        try {
            logger.info("Delete by ID: {} and Date: {}", id,date);

            Optional<SensorData> data = sensorDataService.findBySensorDataID(new SensorDataId(id, date));
            if (!data.isPresent())
            {
                //data not found return an error
                throw new SensorDataException("Unable to delete record: NOT FOUND");
            }
            else {
                List<Optional<SensorData>> dataList = List.of(data);
                for (Optional<SensorData> sd : dataList) {
                    sd.ifPresent(sensorData -> sensorDataService.deleteRecord(sensorData));
                }

                Map<String, List<String>> response = new HashMap<>(1);
                List<String> results = new ArrayList<>();
                results.add("Record deleted");
                response.put(RESULT_TITLE, results);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
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
    public ResponseEntity<Object> createSensorDataTable() throws URISyntaxException, AwsPropertiesException {
        if (    isEmpty(awsProperties.getRegion()) ||
                isEmpty(awsProperties.getAccessKey()) ||
                isEmpty(awsProperties.getSecretKey()))
        {
            throw new AwsPropertiesException("Unable to get AWS Properties");
        }
        logger.info("Creating SensorData table in DynamoDB...");
        AwsCredentialsProvider creds = StaticCredentialsProvider.create(AwsBasicCredentials.create(awsProperties.getAccessKey(), awsProperties.getSecretKey()));

        DynamoDbClient dbClient;
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
            Map<String, List<String>> response = new HashMap<>(1);
            List<String> result = new ArrayList<>();
            result.add(responseText);
            response.put(RESULT_TITLE,result);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (SensorDataException e)
        {
            logger.error(e.getMessage());
            Map<String, List<String>> response = new HashMap<>(1);
            List<String> errors = new ArrayList<>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            response.put(ERROR_TITLE, errors);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private String createSensorDataTable(DynamoDbTable<SensorData> sensorDataDynamoDbTable, DynamoDbClient dynamoDbClient) throws SensorDataException{
        // Create the DynamoDB table by using the 'customerDynamoDbTable' DynamoDbTable instance.
        try{
            sensorDataDynamoDbTable.createTable(builder -> builder
                        .globalSecondaryIndices(gsi -> gsi.indexName("name-date-index")
                                // 3. Populate the GSI with all attributes.
                                .projection(p -> p
                                        .projectionType(ProjectionType.ALL))
                                .provisionedThroughput(b -> b
                                        .readCapacityUnits(10L)
                                        .writeCapacityUnits(10L)
                                        .build())
                        )
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
                            .waitUntilTableExists(builder -> builder.tableName(TABLE_NAME).build())
                            .matched();
                    DescribeTableResponse tableDescription = response.response().orElseThrow(
                            () -> new RuntimeException("SensorData table was not created."));
                    // The actual error can be inspected in response.exception()
                    logger.info("Table {} created. {}", TABLE_NAME, tableDescription);
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

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SensorDataException.class)
    public String returnInternalServerError(SensorDataException ex) {
        logger.error("SensorDataException", ex);
        return ex.getMessage();

    }
}
