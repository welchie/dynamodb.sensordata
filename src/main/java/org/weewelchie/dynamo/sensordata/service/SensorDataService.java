package org.weewelchie.dynamo.sensordata.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.weewelchie.dynamo.sensordata.exception.SensorDataException;
import org.weewelchie.dynamo.sensordata.model.SensorData;
import org.weewelchie.dynamo.sensordata.model.SensorDataId;
import org.weewelchie.dynamo.sensordata.repositories.SensorDataRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SensorDataService {

    private static final Logger logger = LoggerFactory.getLogger(SensorDataService.class);

    @Autowired
    SensorDataRepository sensorDataRepo;

    /**
     * Finds all SensorData Records
     * @return List of SensorData
     */
    public List<SensorData> findAll()
    {
        logger.info("Finding All SensorData records");
        return (List<SensorData>) sensorDataRepo.findAll();
    }

    /**
     * Find SensorData by ID (Primary Key)
     * @param id Primary key of SensorData
     * @return List of SensorData
     * @throws SensorDataException
     */
    public List<SensorData> findById(String id) throws SensorDataException {
        if (id.isEmpty())
            throw new SensorDataException("ID parameter is empty");
        else
        {
            logger.info("Finding SensorData records by ID: {} " , id);
            return sensorDataRepo.findById(id);
        }
    }

    /**
     * Find SensorData by Composit Key from SensorDataID class
     * @param sensorDataId
     * @return Optional List of SensorData
     * @throws SensorDataException
     */
    public Optional<SensorData> findBySensorDataID(SensorDataId sensorDataId) throws SensorDataException {
        if (sensorDataId == null || sensorDataId.getId().isEmpty() || sensorDataId.getDate().isEmpty())
            throw new SensorDataException("SensorDataID record does not contain the appropriate values");
        else
        {
            logger.info("Finding SensorData by SensorDataID: {}", sensorDataId);
            return sensorDataRepo.findById(sensorDataId);
        }
    }

    /**
     * Find SensorData by Date String
     * @param date
     * @return List of SensorData
     * @throws SensorDataException
     */
    public List<SensorData> findByDate(String date) throws SensorDataException {
        if (date.isEmpty())
            throw new SensorDataException("Date parameter is empty");
        else {
            logger.info("Finding SensorData by date: {}", date);
            return sensorDataRepo.findByDate(date);
        }
    }

    /**
     * Find SensorData between dates
     * @param startDate
     * @param endDate
     * @return List of SensorData
     * @throws SensorDataException
     */
    public List<SensorData> findBetweenDates(String startDate, String endDate) throws SensorDataException {
        if (startDate.isEmpty() || endDate.isEmpty())
            throw new SensorDataException("StartDate and EndDate must not be blank");
        else
        {
            logger.info("Finding SensorData records between: {} and {}",startDate,endDate);
            return sensorDataRepo.findByDateBetween(startDate,endDate);
        }
    }

    /**
     * Creates a new SensorData record
     * NOTE if the record exists then the existing record will be returned.
     * @param sensorData
     * @return
     * @throws SensorDataException
     */
    public SensorData createRecord(SensorData sensorData) throws SensorDataException {
        if (sensorData.getId() == null || sensorData.getId().trim().equals("")|| sensorData.getDate()== null || sensorData.getDate().trim().equals(""))
        {
            throw new SensorDataException("SensorData ID and Date must be entered to create data");
        }
        else
        {
            logger.info("Creating new SensorData record: {}",sensorData);
            return sensorDataRepo.save(sensorData);
        }
    }


    /**
     * Deletes the Sensor Data record
     * @param sensorData
     */
    public void deleteRecord(SensorData sensorData)
    {
        logger.info("Deleting SensorData: {}",sensorData);
        sensorDataRepo.delete(sensorData);
    }



}
