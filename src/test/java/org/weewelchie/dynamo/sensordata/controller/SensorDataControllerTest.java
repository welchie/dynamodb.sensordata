package org.weewelchie.dynamo.sensordata.controller;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
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
        restTemplate.getForEntity(SENSORDATA_URL + CREATE_TABLE,String.class);
        //Create some Dummy Data
        for (int i=0;i< 60; i++) {
            restTemplate.getForEntity(SENSORDATA_URL + "/create?id="+ID+
                                                            "&date=" + DATE  + String.format("%02d", i) +
                                                            "&tempC=" + TEMP_C +
                                                            "&tempF=" + TEMP_F, String.class);
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
    public void findAll() {
        ResponseEntity<String> response = restTemplate.getForEntity(SENSORDATA_URL + "/all", String.class);

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
        ResponseEntity<String> response = restTemplate.getForEntity(SENSORDATA_URL + "/" + ID + "/" +testDate, String.class);

        assertThat(response.getStatusCode(),is(HttpStatus.OK));
        String body = response.getBody();
        assertThat(body, containsString(ID));
        assertThat(body, containsString(testDate));

    }

    @Test
    public void findByIdAndDateNotFound()
    {
        ResponseEntity<String> response = restTemplate.getForEntity(SENSORDATA_URL + "/" + ID + "/" + DATE, String.class);
        assertThat(response.getStatusCode(),is(HttpStatus.NOT_FOUND));
    }

        @Test
    public void findByID()
    {
        ResponseEntity<String> response = restTemplate.getForEntity(SENSORDATA_URL + "/" + ID , String.class);

        assertThat(response.getStatusCode(),is(HttpStatus.OK));
        String body = response.getBody();
        assertThat(body, containsString(ID));
    }

    @Test
    public void findByIDNotFound()
    {
        ResponseEntity<String> response = restTemplate.getForEntity(SENSORDATA_URL + "/DUMMY_ID", String.class);

        assertThat(response.getStatusCode(),is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void findBetweenDates()
    {
        String startDate = DATE + "00";
        String endDate = DATE + "01";
        ResponseEntity<String> response = restTemplate.getForEntity(SENSORDATA_URL + "/findbetween?startDate=" + startDate + "&endDate=" + endDate, String.class);

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
        ResponseEntity<String> response = restTemplate.getForEntity(SENSORDATA_URL + "/findbetween?startDate=" + date + "&endDate=" + date, String.class);

        assertThat(response.getStatusCode(),is(HttpStatus.NOT_FOUND));

    }

    @Test
    public void findByDate()
    {
        String date = DATE + "01";
        ResponseEntity<String> response = restTemplate.getForEntity(SENSORDATA_URL + "/find?date=" + date, String.class);
        assertThat(response.getStatusCode(),is(HttpStatus.OK));
        String body = response.getBody();
        assertThat(body, containsString(ID));
        assertThat(body, containsString(date));
    }

    @Test
    public void findByDateNotFound()
    {
        String date = "1900-01-01 00:00:00";
        ResponseEntity<String> response = restTemplate.getForEntity(SENSORDATA_URL + "/find?date=" + date, String.class);
        assertThat(response.getStatusCode(),is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void createRecord()
    {
        String testDate = "2020-12-31 23:59:59";
        String testID = "TEST-ID";
        String tempC = "27.4";
        String tempF = "70.12";

        ResponseEntity<String> response = restTemplate.getForEntity(SENSORDATA_URL + "/create?id="+testID+
                "&date=" + testDate +
                "&tempC=" + tempC +
                "&tempF=" + tempF, String.class);
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
        ResponseEntity<String> response = restTemplate.getForEntity(SENSORDATA_URL + "/create?id="+ " " +
                "&date=" + " " +
                "&tempC=" + TEMP_C +
                "&tempF=" + TEMP_F, String.class);
        assertThat(response.getStatusCode(),is(HttpStatus.INTERNAL_SERVER_ERROR));

    }

    @Test
    public void deleteRecord()
    {
        String date = DATE + "01";
        restTemplate.delete(SENSORDATA_URL + "/delete/"+ ID + "/" + date, String.class);

        //Try to find the record that was deleted this should return not found
        ResponseEntity<String> response = restTemplate.getForEntity(SENSORDATA_URL + "/" + ID + "/" +date, String.class);
        assertThat(response.getStatusCode(),is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void deleteRecordError()
    {
        restTemplate.delete(SENSORDATA_URL + "/delete/"+ "DUMMY_ID" + "/1900-01-01 00:00:00" , String.class);
        //assertThat(response.getStatusCode(),is(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
