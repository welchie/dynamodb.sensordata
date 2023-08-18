package org.weewelchie.dynamo.sensordata.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/version")
public class VersionController {

    private static final String VERSION = "0.0.3-SNAPSHOT";
    @GetMapping(value = "/get")
    public String getVersion()
    {
        return VERSION;
    }
}
