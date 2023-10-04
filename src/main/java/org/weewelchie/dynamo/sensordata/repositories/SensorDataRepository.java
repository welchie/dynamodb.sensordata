package org.weewelchie.dynamo.sensordata.repositories;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.weewelchie.dynamo.sensordata.model.SensorData;
import org.weewelchie.dynamo.sensordata.model.SensorDataId;
import java.util.List;


@EnableScan
public interface SensorDataRepository extends CrudRepository<SensorData, SensorDataId> {


    List<SensorData> findByDate(String date);

    List<SensorData> findByDateBetween(String startDate,String endDate);

    List<SensorData> findById(String id);

    List<SensorData> findByName(String name);

    List<SensorData> findByNameAndDateBetween(String name,String startDate, String endDate);

    List<SensorData> findByNameAndDate(String name, String date);
}
