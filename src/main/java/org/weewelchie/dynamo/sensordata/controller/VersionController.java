package org.weewelchie.dynamo.sensordata.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/version")
public class VersionController {

    private static final Logger logger = LoggerFactory.getLogger(VersionController.class);
    @GetMapping(value = "/get")
    public ResponseEntity<?> getVersion() {
        logger.info("Reading version from Manifest file");

        try {
            Class<? extends VersionController> clazz = this.getClass();
            String className = clazz.getSimpleName() + ".class";
            String classPath = clazz.getResource(className).toString();
            if (!classPath.startsWith("jar")) {
                // Class not from JAR
                String err= "Cannot read version from the Manifest file";
                logger.error(err);
                throw new Exception(err);
            }
            String manifestPath = classPath.substring(0, classPath.indexOf("!") + 1) +
                    "/META-INF/maven/org.weewelchie/dynamo.sensordata/pom.properties";

            byte[] bytes;
            try (InputStream inputStream = new URL(manifestPath).openStream()) {
                bytes = inputStream.readAllBytes();
            }

            StringBuilder data = new StringBuilder();
            for(int i=0; i < bytes.length;i++) {
                data.append((char) bytes[i]);
            }

            Map<String, String> info = new HashMap<String, String>(1);
            String data2 = data.toString();
            String[] lines = data2.split("\n");
            for(String str:lines)
            {
                String[] line = str.split("=");
                if(line.length == 2)
                {
                    info.put(line[0],line[1].substring(0,line[1].length()-1));
                }
            }

            return new ResponseEntity<Map<String, String>>(info, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, List<String>> response = new HashMap<String, List<String>>(1);
            List<String> errors = new ArrayList<String>();
            errors.add(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            response.put("errors", errors);
            return new ResponseEntity<Map<String, List<String>>>(response, HttpStatus.BAD_GATEWAY);
        }

    }
}
