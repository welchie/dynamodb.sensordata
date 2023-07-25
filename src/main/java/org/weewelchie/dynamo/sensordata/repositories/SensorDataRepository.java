package org.weewelchie.dynamo.sensordata.repositories;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.weewelchie.dynamo.sensordata.model.SensorData;

import java.util.Optional;

@EnableScan
public interface SensorDataRepository extends CrudRepository<SensorData,String> {
    Optional<SensorData> findBySensorId(String id);

    Optional<SensorData> findById(String id);
}
