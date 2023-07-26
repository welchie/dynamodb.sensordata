package org.weewelchie.dynamo.sensordata.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import org.springframework.data.annotation.Id;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.Objects;

@DynamoDbBean
@DynamoDBTable(tableName = "SensorData")
public class SensorData {



    @Id
    private SensorDataId sensorId;
    public SensorData(String id, String date, String tempC, String tempF) {
        this.id = id;
        this.date = date;
        this.tempC = tempC;
        this.tempF = tempF;
    }

    public SensorData() {}

    private String id;
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
    public String getDate() {
        return date;
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
        return Objects.equals(id, that.id) && Objects.equals(date, that.date) && Objects.equals(tempC, that.tempC) && Objects.equals(tempF, that.tempF);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, tempC, tempF);
    }

    @Override
    public String toString() {
        return "SensorData{" +
                //"sensorId='" + sensorId + '\'' +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", tempC='" + tempC + '\'' +
                ", tempF='" + tempF + '\'' +
                '}';
    }
}
