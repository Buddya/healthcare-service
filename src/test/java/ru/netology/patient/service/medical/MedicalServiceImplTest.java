package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MedicalServiceTest {
    private final SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);
    private final MedicalService medicalService = infoRepositoryForMedicalServiceMock();

    private MedicalService infoRepositoryForMedicalServiceMock() {
        PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        BloodPressure bloodPressure = new BloodPressure(120, 75);
        BigDecimal normalTemperature = new BigDecimal("36.6");
        HealthInfo healthInfo = new HealthInfo(normalTemperature, bloodPressure);
        PatientInfo patientInfo = new PatientInfo("patientZero", null, null, null, healthInfo);

        Mockito.when(patientInfoRepository.getById("patientZero")).thenReturn(patientInfo);

        return new MedicalServiceImpl(patientInfoRepository, sendAlertService);
    }

    @Test
    void checkBloodPressureNormal() {
        // given:
        String patientId = "patientZero";
        BloodPressure bloodPressure = new BloodPressure(120, 75);

        // when:
        medicalService.checkBloodPressure(patientId, bloodPressure);

        // then:
        Mockito.verify(sendAlertService, Mockito.never()).send(Mockito.anyString());
    }

    @Test
    void checkBloodPressureChanged() {
        // given:
        String patientId = "patientZero";
        BloodPressure bloodPressure = new BloodPressure(150, 70);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        String expectedMessage = "Warning, patient with id: patientZero, need help";

        // when:
        medicalService.checkBloodPressure(patientId, bloodPressure);

        // then:
        Mockito.verify(sendAlertService, Mockito.only()).send(Mockito.anyString());
        Mockito.verify(sendAlertService).send(argumentCaptor.capture());
        assertEquals(expectedMessage, argumentCaptor.getValue());
    }

    @Test
    void checkTemperatureNormal() {
        // given:
        String patientId = "patientZero";
        BigDecimal currentTemperature = new BigDecimal("36.6");

        // when:
        medicalService.checkTemperature(patientId, currentTemperature);

        // then:
        Mockito.verify(sendAlertService, Mockito.never()).send(Mockito.anyString());
    }

    @Test
    void checkTemperatureChanged() {
        // given:
        String patientId = "patientZero";
        //BigDecimal currentTemperature = new BigDecimal("39.9"); ??? doesn't work with high temperature? :D
        BigDecimal currentTemperature = new BigDecimal("30.9");
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        String expectedMessage = "Warning, patient with id: patientZero, need help";

        // when:
        medicalService.checkTemperature(patientId, currentTemperature);

        // then:
        Mockito.verify(sendAlertService, Mockito.only()).send(Mockito.anyString());
        Mockito.verify(sendAlertService).send(argumentCaptor.capture());
        assertEquals(expectedMessage, argumentCaptor.getValue());
    }
}