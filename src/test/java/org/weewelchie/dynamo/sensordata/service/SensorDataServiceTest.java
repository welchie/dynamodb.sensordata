package org.weewelchie.dynamo.sensordata.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.weewelchie.dynamo.sensordata.exception.SensorDataException;
import org.weewelchie.dynamo.sensordata.model.SensorData;
import org.weewelchie.dynamo.sensordata.model.SensorDataId;
import org.weewelchie.dynamo.sensordata.repositories.SensorDataRepository;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SensorDataServiceTest {

@Mock
private SensorDataRepository sensorDataRepositoryMock;

@InjectMocks
private SensorDataService service;

@Mock
private SensorData sensorDataMock;

@Mock
private SensorDataId sensorDataIdMock;

private static final String ID = "TEMP-1234567890";
private static final String START_DATE = "2020-01-01 00:00:00";

    private static final String END_DATE = "2020-01-31 23:59:59";

    private static final SensorDataId SENSOR_DATA_ID = new SensorDataId(ID,START_DATE);

    @Before
    public void setupReturnValuesOfMockMethods() {
        when(sensorDataRepositoryMock.findById(ID)).thenReturn(List.of(sensorDataMock));
        when(sensorDataRepositoryMock.findAll()).thenReturn(List.of(sensorDataMock));
        when(sensorDataRepositoryMock.findByDate(START_DATE)).thenReturn(List.of(sensorDataMock));
        when(sensorDataRepositoryMock.findByDateBetween(START_DATE,END_DATE)).thenReturn(List.of(sensorDataMock));
        when(sensorDataRepositoryMock.findById(sensorDataIdMock)).thenReturn(Optional.of(sensorDataMock));
        when(sensorDataIdMock.getId()).thenReturn(ID);
        when(sensorDataIdMock.getDate()).thenReturn(START_DATE);
    }
    @Test
    public void findById() throws SensorDataException {
        //invoke and verify lookupRatingById
        assertThat(service.findById(ID), is(List.of(sensorDataMock)));
    }

    @Test
    public void findByIDNoData() throws SensorDataException {

        Exception exception = assertThrows(SensorDataException.class, ()-> {
            //invoke createNew wih no data
            service.findById("");
        });

        String expectedMessage = "ID parameter is empty";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void findBySensorDataId() throws SensorDataException {
        //invoke and verify lookupRatingById
        SensorDataId sensorDataId = new SensorDataId(ID,START_DATE);

        assertThat(service.findBySensorDataID(sensorDataIdMock), is(Optional.of(sensorDataMock)));
    }

    @Test
    public void findBySensorDataIdNoData() throws SensorDataException {

        Exception exception = assertThrows(SensorDataException.class, ()-> {
            //invoke createNew wih no data
            service.findBySensorDataID(new SensorDataId("",""));
        });

        String expectedMessage = "SensorDataID record does not contain the appropriate values";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void findBySensorDataIdNull() throws SensorDataException {

        Exception exception = assertThrows(SensorDataException.class, ()-> {
            //invoke createNew wih no data
            service.findBySensorDataID(null);
        });

        String expectedMessage = "SensorDataID record does not contain the appropriate values";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
    @Test
    public void findAll() {
        //invoke and verify findAll
        assertThat(service.findAll().get(0), is(sensorDataMock));
    }
    @Test
    public void findByDate() throws SensorDataException {
        assertThat(service.findByDate(START_DATE).get(0),is(sensorDataMock));
    }

    @Test
    public void findByDateNoData() {

        Exception exception = assertThrows(SensorDataException.class, ()-> {
            //invoke createNew wih no data
            service.findByDate("");
        });

        String expectedMessage = "Date parameter is empty";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
    @Test
    public void findBetweenDates() throws SensorDataException {
        assertThat(service.findBetweenDates(START_DATE,END_DATE).get(0),is(sensorDataMock));
    }

    @Test
    public void findBetweenDatesNoValues() throws SensorDataException {

        Exception exception = assertThrows(SensorDataException.class, ()-> {
            //invoke createNew wih no data
            service.findBetweenDates("","");
        });

        String expectedMessage = "StartDate and EndDate must not be blank";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void findBetweenDatesNoStartDate() throws SensorDataException {

        Exception exception = assertThrows(SensorDataException.class, ()-> {
            //invoke createNew wih no data
            service.findBetweenDates("",END_DATE);
        });

        String expectedMessage = "StartDate and EndDate must not be blank";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void findBetweenDatesNoEndDate() throws SensorDataException {

        Exception exception = assertThrows(SensorDataException.class, ()-> {
            //invoke createNew wih no data
            service.findBetweenDates(START_DATE,"");
        });

        String expectedMessage = "StartDate and EndDate must not be blank";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void createRecord() throws SensorDataException {
        //prepare to capture a SensorData Object
        ArgumentCaptor<SensorData> sensorDataCaptor = ArgumentCaptor.forClass(SensorData.class);

        //invoke createNew
        SensorData sensorData = new SensorData(ID,START_DATE,"12.25","68.13");
        //assertThat(service.createRecord(sensorData),is(sensorDataTest));

        service.createRecord(sensorData);
        //verify tourRatingRepository.save invoked once and capture the TourRating Object
        verify(sensorDataRepositoryMock).save(sensorDataCaptor.capture());

        //verify the attributes of the Tour Rating Object
        assertThat(sensorDataCaptor.getValue().getId(), is(ID));
        assertThat(sensorDataCaptor.getValue().getDate(), is(START_DATE));
        assertThat(sensorDataCaptor.getValue().getTempC(), is("12.25"));
        assertThat(sensorDataCaptor.getValue().getTempF(), is("68.13"));
    }

    @Test
    public void createRecordInvalidData() throws SensorDataException
    {
        Exception exception = assertThrows(SensorDataException.class, ()-> {
            //invoke createNew wih no data
            service.createRecord(new SensorData());
        });

        String expectedMessage = "SensorData ID and Date must be entered to create data";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
    @Test
    public void delete() {
        //invoke delete
        service.deleteRecord(sensorDataMock);

        //verify tourRatingRepository.delete invoked
        verify(sensorDataRepositoryMock).delete(any(SensorData.class));
    }

}
