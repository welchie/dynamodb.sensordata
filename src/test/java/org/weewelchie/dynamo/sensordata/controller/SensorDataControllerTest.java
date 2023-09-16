package org.weewelchie.dynamo.sensordata.controller;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Profile("test")
public class SensorDataControllerTest {

    @Autowired
    TestRestTemplate restTemplate;


    private static final String SENSORDATA_URL = "/sensordata";

    private static final String CREATE_TABLE = "/admin/create";

    private static final String ID = "TEST-1234";
    private static final String DATE = "2020-01-01 00:00:";
    private static final String TEMP_C = "12.25";
    private static final String TEMP_F = "68.15";
    private static DynamoDBProxyServer server;

    private static HttpHeaders authzHeaders = new HttpHeaders();

    @BeforeClass
    public static void setupClass() throws Exception {
        //Set up local DynamoDB Instance
        System.setProperty("sqlite4java.library.path", "native-libs");
        String port = "8000";
        server = ServerRunner.createServerFromCommandLineArgs(
                new String[]{"-inMemory", "-port", port});
        server.start();
    }

    @AfterClass
    public static void teardownClass() throws Exception {
        server.stop();
    }

    @Before
    public void setUp() throws Exception {
        //Setup SensorData table in Local DynamoDB
        authzHeaders.add("Authorization", "Basic dXNlcjpwYXNzd29yZA==");
        authzHeaders.add("Content-Type", "application/json");

        //restTemplate.getForEntity(SENSORDATA_URL + CREATE_TABLE,String.class);
        ResponseEntity<String> response = restTemplate.exchange(SENSORDATA_URL + CREATE_TABLE,
                HttpMethod.GET,
                new HttpEntity<>(null, authzHeaders),
                String.class
        );
        //Create some Dummy Data
        for (int i=0;i< 60; i++) {
            response = restTemplate.exchange(SENSORDATA_URL + "/create?id="+ID+
                                                            "&date=" + DATE  + String.format("%02d", i) +
                                                            "&tempC=" + TEMP_C +
                                                            "&tempF=" + TEMP_F,
                    HttpMethod.GET,
                    new HttpEntity<>(null,authzHeaders),
                    String.class
            );
        }
    }

    @After
    public void afterTest()
    {
        for (int i=0;i<60;i++)
        {
            restTemplate.delete(SENSORDATA_URL + "/delete/"+ID+ "/" + DATE + String.format("%02d",i),String.class);
        }
    }

    @Test
    public void findAll()
    {

        ResponseEntity<String> response = restTemplate.exchange(SENSORDATA_URL + "/all",
                HttpMethod.GET,
                new HttpEntity<>(null, authzHeaders),
                String.class
        );

        assertThat(response.getStatusCode(),is(HttpStatus.OK));
        String body = response.getBody();
        assertThat(body, containsString(ID));
        assertThat(body, containsString(DATE));
        assertThat(body, containsString(TEMP_C));
        assertThat(body, containsString(TEMP_F));
    }



    @Test
    public void findByIdAndDate()
    {
        String testDate = DATE + "01";
        ResponseEntity<String> response = restTemplate.exchange(SENSORDATA_URL + "/" + ID + "/" +testDate,
                HttpMethod.GET,
                new HttpEntity<>(null, authzHeaders),
                String.class
        );

        assertThat(response.getStatusCode(),is(HttpStatus.OK));
        String body = response.getBody();
        assertThat(body, containsString(ID));
        assertThat(body, containsString(testDate));

    }

    @Test
    public void findByIdAndDateNotFound()
    {

        ResponseEntity<String> response = restTemplate.exchange(SENSORDATA_URL + "/" + ID + "/" + DATE,
                HttpMethod.GET,
                new HttpEntity<>(null, authzHeaders),
                String.class
        );
        assertThat(response.getStatusCode(),is(HttpStatus.NOT_FOUND));
    }

        @Test
    public void findByID()
    {
        ResponseEntity<String> response = restTemplate.exchange(SENSORDATA_URL + "/" + ID ,
                HttpMethod.GET,
                new HttpEntity<>(null, authzHeaders),
                String.class
        );
        assertThat(response.getStatusCode(),is(HttpStatus.OK));
        String body = response.getBody();
        assertThat(body, containsString(ID));
    }

    @Test
    public void findByIDNotFound()
    {
        ResponseEntity<String> response = restTemplate.exchange(SENSORDATA_URL + "/DUMMY_ID" ,
                HttpMethod.GET,
                new HttpEntity<>(null, authzHeaders),
                String.class
        );
        assertThat(response.getStatusCode(),is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void findBetweenDates()
    {
        String startDate = DATE + "00";
        String endDate = DATE + "01";
        ResponseEntity<String> response = restTemplate.exchange(SENSORDATA_URL + "/findbetween?startDate=" + startDate + "&endDate=" + endDate ,
            HttpMethod.GET,
            new HttpEntity<>(null, authzHeaders),
            String.class
        );

        assertThat(response.getStatusCode(),is(HttpStatus.OK));
        String body = response.getBody();
        assertThat(body, containsString(ID));
        assertThat(body, containsString(startDate));
        assertThat(body, containsString(endDate));
    }

    @Test
    public void findBetweenDatesNotFound()
    {
        String date = "1900-01-01 00:00:00";
        ResponseEntity<String> response = restTemplate.exchange(SENSORDATA_URL + "/findbetween?startDate=" + date + "&endDate=" + date ,
                HttpMethod.GET,
                new HttpEntity<>(null, authzHeaders),
                String.class
        );
        assertThat(response.getStatusCode(),is(HttpStatus.NOT_FOUND));

    }

    @Test
    public void findByDate()
    {
        String date = DATE + "01";
        ResponseEntity<String> response = restTemplate.exchange(SENSORDATA_URL + "/find?date=" + date ,
                HttpMethod.GET,
                new HttpEntity<>(null, authzHeaders),
                String.class
        );
        assertThat(response.getStatusCode(),is(HttpStatus.OK));
        String body = response.getBody();
        assertThat(body, containsString(ID));
        assertThat(body, containsString(date));
    }

    @Test
    public void findByDateNotFound()
    {
        String date = "1900-01-01 00:00:00";
        ResponseEntity<String> response = restTemplate.exchange(SENSORDATA_URL + "/find?date=" + date ,
                HttpMethod.GET,
                new HttpEntity<>(null, authzHeaders),
                String.class
        );
        assertThat(response.getStatusCode(),is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void createRecord()
    {
        String testDate = "2020-12-31 23:59:59";
        String testID = "TEST-ID";
        String tempC = "27.4";
        String tempF = "70.12";

        ResponseEntity<String> response = restTemplate.exchange(SENSORDATA_URL + "/create?id="+testID+
                        "&date=" + testDate +
                        "&tempC=" + tempC +
                        "&tempF=" + tempF ,
                HttpMethod.GET,
                new HttpEntity<>(null, authzHeaders),
                String.class
        );
        assertThat(response.getStatusCode(),is(HttpStatus.OK));
        String body = response.getBody();
        assertThat(body, containsString(testID));
        assertThat(body, containsString(testDate));
        assertThat(body, containsString(tempC));
        assertThat(body, containsString(tempF));
    }

    @Test
    public void createRecordError()
    {
        ResponseEntity<String> response = restTemplate.exchange(SENSORDATA_URL + "/create?id="+ " " +
                        "&date=" + " " +
                        "&tempC=" + TEMP_C +
                        "&tempF=" + TEMP_F ,
                HttpMethod.GET,
                new HttpEntity<>(null, authzHeaders),
                String.class
        );
        assertThat(response.getStatusCode(),is(HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @Test
    public void deleteRecord()
    {
        String date = DATE + "01";
        ResponseEntity<String> response = restTemplate.exchange(SENSORDATA_URL + "/delete/"+ ID + "/" + date ,
                HttpMethod.DELETE,
                new HttpEntity<>(null, authzHeaders),
                String.class
        );

        //Try to find the record that was deleted this should return not found
       response = restTemplate.exchange(SENSORDATA_URL + "/" + ID + "/" +date ,
                HttpMethod.GET,
                new HttpEntity<>(null, authzHeaders),
                String.class
        );
        assertThat(response.getStatusCode(),is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void deleteRecordError()
    {
        ResponseEntity<String> response = restTemplate.exchange(SENSORDATA_URL + "/delete/"+ "DUMMY_ID" + "/1900-01-01 00:00:00" ,
                HttpMethod.DELETE,
                new HttpEntity<>(null, authzHeaders),
                String.class
        );
        //assertThat(response.getStatusCode(),is(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
