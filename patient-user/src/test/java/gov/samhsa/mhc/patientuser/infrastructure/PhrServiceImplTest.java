package gov.samhsa.mhc.patientuser.infrastructure;

import feign.FeignException;
import gov.samhsa.mhc.patientuser.infrastructure.dto.PatientDto;
import gov.samhsa.mhc.patientuser.infrastructure.exception.PhrPatientNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PhrServiceImplTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private PhrServiceDefault phrServiceDefault;

    @Mock
    private PhrServiceClientCredentials phrServiceClientCredentials;

    @InjectMocks
    private PhrServiceImpl sut;

    @Before
    public void setup() {
    }

    @Test
    public void testFindPatientProfileById() throws Exception {
        // Arrange
        final Long patientId = 5L;
        final PatientDto patientDto = mock(PatientDto.class);
        when(phrServiceDefault.findPatientProfileById(patientId)).thenReturn(patientDto);

        // Act
        final PatientDto response = sut.findPatientProfileById(patientId);

        // Assert
        assertEquals(patientDto, response);
        verify(phrServiceDefault, times(1)).findPatientProfileById(patientId);
        verify(phrServiceClientCredentials, times(0)).findPatientProfileById(patientId);
    }

    @Test
    public void testFindPatientProfileById_Use_Client_Credentials_False() throws Exception {
        // Arrange
        final boolean useClientCredentials = false;
        final Long patientId = 5L;
        final PatientDto patientDto = mock(PatientDto.class);
        when(phrServiceDefault.findPatientProfileById(patientId)).thenReturn(patientDto);

        // Act
        final PatientDto response = sut.findPatientProfileById(patientId, useClientCredentials);

        // Assert
        assertEquals(patientDto, response);
        verify(phrServiceDefault, times(1)).findPatientProfileById(patientId);
        verify(phrServiceClientCredentials, times(0)).findPatientProfileById(patientId);
    }

    @Test
    public void testFindPatientProfileById_Use_Client_Credentials_False_Throws_PhrPatientNotFoundException() throws Exception {
        // Arrange
        final boolean useClientCredentials = false;
        final Long patientId = 5L;
        final PatientDto patientDto = mock(PatientDto.class);
        FeignException e = mock(FeignException.class);
        when(e.status()).thenReturn(HttpStatus.NOT_FOUND.value());
        when(phrServiceDefault.findPatientProfileById(patientId)).thenThrow(e);
        thrown.expect(PhrPatientNotFoundException.class);

        // Act
        final PatientDto response = sut.findPatientProfileById(patientId, useClientCredentials);

        // Assert
        assertEquals(patientDto, response);
        verify(phrServiceDefault, times(1)).findPatientProfileById(patientId);
        verify(phrServiceClientCredentials, times(0)).findPatientProfileById(patientId);
    }

    @Test
    public void testFindPatientProfileById_Use_Client_Credentials_False_Throws_FeignException() throws Exception {
        // Arrange
        final boolean useClientCredentials = false;
        final Long patientId = 5L;
        final PatientDto patientDto = mock(PatientDto.class);
        FeignException e = mock(FeignException.class);
        when(e.status()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());
        when(phrServiceDefault.findPatientProfileById(patientId)).thenThrow(e);
        thrown.expect(FeignException.class);

        // Act
        final PatientDto response = sut.findPatientProfileById(patientId, useClientCredentials);

        // Assert
        assertEquals(patientDto, response);
        verify(phrServiceDefault, times(1)).findPatientProfileById(patientId);
        verify(phrServiceClientCredentials, times(0)).findPatientProfileById(patientId);
    }

    @Test
    public void testFindPatientProfileById_Use_Client_Credentials_False_Throws_RuntimeException() throws Exception {
        // Arrange
        final boolean useClientCredentials = false;
        final Long patientId = 5L;
        final PatientDto patientDto = mock(PatientDto.class);
        when(phrServiceDefault.findPatientProfileById(patientId)).thenThrow(RuntimeException.class);
        thrown.expect(RuntimeException.class);

        // Act
        final PatientDto response = sut.findPatientProfileById(patientId, useClientCredentials);

        // Assert
        assertEquals(patientDto, response);
        verify(phrServiceDefault, times(1)).findPatientProfileById(patientId);
        verify(phrServiceClientCredentials, times(0)).findPatientProfileById(patientId);
    }

    @Test
    public void testFindPatientProfileById_Use_Client_Credentials_True() throws Exception {
        // Arrange
        final boolean useClientCredentials = true;
        final Long patientId = 5L;
        final PatientDto patientDto = mock(PatientDto.class);
        when(phrServiceClientCredentials.findPatientProfileById(patientId)).thenReturn(patientDto);

        // Act
        final PatientDto response = sut.findPatientProfileById(patientId, useClientCredentials);

        // Assert
        assertEquals(patientDto, response);
        verify(phrServiceDefault, times(0)).findPatientProfileById(patientId);
        verify(phrServiceClientCredentials, times(1)).findPatientProfileById(patientId);
    }
}