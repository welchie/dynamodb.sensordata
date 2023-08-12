package org.weewelchie.dynamo.sensordata.authentication;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import jakarta.servlet.http.HttpServletRequest;
import org.weewelchie.dynamo.sensordata.controller.SensorDataController;
import org.weewelchie.dynamo.sensordata.utils.SensorDataUtils;

import java.util.Properties;

public class AuthenticationService {

    private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";
    private static final String AUTH_TOKEN = "weewelchie";

    private static final String SENSORDATA_API_KEY = "sensordata.api.key";

    public static Authentication getAuthentication(HttpServletRequest request) {

        Properties testProperties = SensorDataUtils.loadFromFileInClasspath("application.properties")
                .filter(properties -> !SensorDataUtils.isEmpty(properties.getProperty(SENSORDATA_API_KEY))).orElseThrow(() -> new RuntimeException("Unable to get all of the required API KEY property values"));

        String sensorDataApiKey = testProperties.getProperty(SENSORDATA_API_KEY);;

        String apiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);
        if (apiKey == null || !apiKey.equals(sensorDataApiKey)) {
            throw new BadCredentialsException("Invalid API Key");
        }

        return new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
    }
}
