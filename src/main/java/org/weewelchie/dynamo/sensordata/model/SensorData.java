package org.weewelchie.dynamo.sensordata.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import org.springframework.data.annotation.Id;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.util.Objects;

@DynamoDbBean
@DynamoDBTable(tableName = "SensorData")
public class SensorData {

    @Id
    private SensorDataId sensorId;
    public SensorData(String id, String name, String date, String tempC, String tempF) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.tempC = tempC;
        this.tempF = tempF;
    }

    public SensorData() {}

    private String id;



    private String name;
    private String date;
    private String tempC;
    private String tempF;

    @DynamoDBHashKey(attributeName = "id")
    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDbSortKey
    @DynamoDBRangeKey(attributeName = "date")
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "name-date-index",attributeName = "date")
    @DynamoDbSecondarySortKey(indexNames = "name-date-index")
    public String getDate() {
        return date;
    }

    @DynamoDBAttribute(attributeName = "name")
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "name-date-index", attributeName = "name")
    @DynamoDbSecondaryPartitionKey(indexNames = "name-date-index")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setDate(String date) {
        this.date = date;
    }

    @DynamoDBAttribute(attributeName = "temp_c")
    public String getTempC() {
        return tempC;
    }


    public void setTempC(String tempC) {
        this.tempC = tempC;
    }

    @DynamoDBAttribute(attributeName = "temp_f")
    public String getTempF() {
        return tempF;
    }

    public void setTempF(String tempF) {
        this.tempF = tempF;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorData that = (SensorData) o;
        return Objects.equals(sensorId, that.sensorId) && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(date, that.date) && Objects.equals(tempC, that.tempC) && Objects.equals(tempF, that.tempF);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sensorId, id, name, date, tempC, tempF);
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", tempC='" + tempC + '\'' +
                ", tempF='" + tempF + '\'' +
                '}';
    }
}
