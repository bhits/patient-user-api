package gov.samhsa.mhc.patientuser.infrastructure;

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
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PhrServiceImplTest {

    private static final String phrApiBaseUri = "phrApiBaseUri";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private OAuth2RestTemplate restTemplate;
    @Mock
    private OAuth2RestTemplate restTemplateWithClientCredentials;
    @InjectMocks
    private PhrServiceImpl sut;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(sut, phrApiBaseUri, phrApiBaseUri);
    }

    @Test
    public void testFindPatientProfileById() throws Exception {
        // Arrange
        final Long patientId = 5L;
        final String url = phrApiBaseUri + "/patients/" + patientId + "/profile";
        final PatientDto patientDto = mock(PatientDto.class);
        when(restTemplate.getForObject(url, PatientDto.class)).thenReturn(patientDto);

        // Act
        final PatientDto response = sut.findPatientProfileById(patientId);

        // Assert
        assertEquals(patientDto, response);
        verify(restTemplate, times(1)).getForObject(url, PatientDto.class);
        verify(restTemplateWithClientCredentials, times(0)).getForObject(url, PatientDto.class);
    }

    @Test
    public void testFindPatientProfileById_Use_Client_Credentials_False() throws Exception {
        // Arrange
        final boolean useClientCredentials = false;
        final Long patientId = 5L;
        final String url = phrApiBaseUri + "/patients/" + patientId + "/profile";
        final PatientDto patientDto = mock(PatientDto.class);
        when(restTemplate.getForObject(url, PatientDto.class)).thenReturn(patientDto);

        // Act
        final PatientDto response = sut.findPatientProfileById(patientId, useClientCredentials);

        // Assert
        assertEquals(patientDto, response);
        verify(restTemplate, times(1)).getForObject(url, PatientDto.class);
        verify(restTemplateWithClientCredentials, times(0)).getForObject(url, PatientDto.class);
    }

    @Test
    public void testFindPatientProfileById_Use_Client_Credentials_False_Throws_PhrPatientNotFoundException() throws Exception {
        // Arrange
        final boolean useClientCredentials = false;
        final Long patientId = 5L;
        final String url = phrApiBaseUri + "/patients/" + patientId + "/profile";
        final PatientDto patientDto = mock(PatientDto.class);
        HttpClientErrorException e = new HttpClientErrorException(HttpStatus.NOT_FOUND);
        when(restTemplate.getForObject(url, PatientDto.class)).thenThrow(e);
        thrown.expect(PhrPatientNotFoundException.class);

        // Act
        final PatientDto response = sut.findPatientProfileById(patientId, useClientCredentials);

        // Assert
        assertEquals(patientDto, response);
        verify(restTemplate, times(1)).getForObject(url, PatientDto.class);
        verify(restTemplateWithClientCredentials, times(0)).getForObject(url, PatientDto.class);
    }

    @Test
    public void testFindPatientProfileById_Use_Client_Credentials_False_Throws_HttpClientErrorException() throws Exception {
        // Arrange
        final boolean useClientCredentials = false;
        final Long patientId = 5L;
        final String url = phrApiBaseUri + "/patients/" + patientId + "/profile";
        final PatientDto patientDto = mock(PatientDto.class);
        HttpClientErrorException e = new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.getForObject(url, PatientDto.class)).thenThrow(e);
        thrown.expect(HttpClientErrorException.class);

        // Act
        final PatientDto response = sut.findPatientProfileById(patientId, useClientCredentials);

        // Assert
        assertEquals(patientDto, response);
        verify(restTemplate, times(1)).getForObject(url, PatientDto.class);
        verify(restTemplateWithClientCredentials, times(0)).getForObject(url, PatientDto.class);
    }

    @Test
    public void testFindPatientProfileById_Use_Client_Credentials_False_Throws_RuntimeException() throws Exception {
        // Arrange
        final boolean useClientCredentials = false;
        final Long patientId = 5L;
        final String url = phrApiBaseUri + "/patients/" + patientId + "/profile";
        final PatientDto patientDto = mock(PatientDto.class);
        when(restTemplate.getForObject(url, PatientDto.class)).thenThrow(RuntimeException.class);
        thrown.expect(RuntimeException.class);

        // Act
        final PatientDto response = sut.findPatientProfileById(patientId, useClientCredentials);

        // Assert
        assertEquals(patientDto, response);
        verify(restTemplate, times(1)).getForObject(url, PatientDto.class);
        verify(restTemplateWithClientCredentials, times(0)).getForObject(url, PatientDto.class);
    }

    @Test
    public void testFindPatientProfileById_Use_Client_Credentials_True() throws Exception {
        // Arrange
        final boolean useClientCredentials = true;
        final Long patientId = 5L;
        final String url = phrApiBaseUri + "/patients/" + patientId + "/profile";
        final PatientDto patientDto = mock(PatientDto.class);
        when(restTemplateWithClientCredentials.getForObject(url, PatientDto.class)).thenReturn(patientDto);

        // Act
        final PatientDto response = sut.findPatientProfileById(patientId, useClientCredentials);

        // Assert
        assertEquals(patientDto, response);
        verify(restTemplate, times(0)).getForObject(url, PatientDto.class);
        verify(restTemplateWithClientCredentials, times(1)).getForObject(url, PatientDto.class);
    }

}