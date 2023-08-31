package org.weewelchie.dynamo.sensordata.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class VersionControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    private static final String VERSION_URL = "/version/get";
    @Test
    public void testGetVersion() {
        ResponseEntity<String> response = restTemplate.getForEntity(VERSION_URL, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_GATEWAY));
        assertThat(response.getBody(), is("{\"errors\":[\"Cannot read version from the Manifest file\"]}"));
    }
}
