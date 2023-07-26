package org.weewelchie.dynamo.sensordata.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;

import java.io.Serializable;

public class SensorDataId implements Serializable {

    private String id;

    private String date;

    public SensorDataId()
    {
        this(null,null);
    }

    public SensorDataId(String id, String date)
    {
        this.id = id;
        this.date = date;
    }

   @DynamoDBHashKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDBRangeKey
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
