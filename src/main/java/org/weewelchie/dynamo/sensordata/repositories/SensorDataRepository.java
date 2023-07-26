package org.weewelchie.dynamo.sensordata.repositories;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import org.weewelchie.dynamo.sensordata.model.SensorData;
import org.weewelchie.dynamo.sensordata.model.SensorDataId;

import java.util.Optional;

@EnableScan
public interface SensorDataRepository extends CrudRepository<SensorData, SensorDataId> {

    Optional<SensorData> findById(SensorDataId id);
}
