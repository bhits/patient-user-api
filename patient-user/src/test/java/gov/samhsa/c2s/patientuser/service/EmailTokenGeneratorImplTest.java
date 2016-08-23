package gov.samhsa.c2s.patientuser.service;

import gov.samhsa.c2s.patientuser.domain.UserCreationRepository;
import gov.samhsa.c2s.common.util.UniqueValueGeneratorException;
import gov.samhsa.c2s.patientuser.domain.UserCreation;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EmailTokenGeneratorImplTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private TokenGenerator tokenGenerator;

    @Mock
    private UserCreationRepository userCreationRepository;

    @InjectMocks
    private EmailTokenGeneratorImpl sut;

    @Test
    public void testGenerateEmailToken() throws Exception {
        // Arrange
        String value = "differentValueFirstTime";
        when(tokenGenerator.generateToken()).thenReturn(value);
        when(userCreationRepository.findOneByEmailToken(value)).thenReturn(Optional.empty());

        // Act
        final String emailToken = sut.generateEmailToken();

        // Assert
        assertEquals(value, emailToken);
        verify(tokenGenerator, times(1)).generateToken();
        verify(userCreationRepository, times(1)).findOneByEmailToken(value);
    }


    @Test
    public void testGenerateEmailToken_Throws_UniqueValueGeneratorException_When_Same_Value_Every_Time() throws Exception {
        // Arrange
        String value = "sameValueEveryTime";
        when(tokenGenerator.generateToken()).thenReturn(value);
        when(userCreationRepository.findOneByEmailToken(value)).thenReturn(Optional.of(mock(UserCreation.class)));
        thrown.expect(UniqueValueGeneratorException.class);

        // Act
        sut.generateEmailToken();

        // Assert
        verify(tokenGenerator, times(3)).generateToken();
        verify(userCreationRepository, times(3)).findOneByEmailToken(value);
    }

    @Test
    public void testGenerateEmailToken_Throws_UniqueValueGeneratorException_When_Non_Unique_Different_Value() throws Exception {
        // Arrange
        String value1 = "differentValueEveryTime1";
        String value2 = "differentValueEveryTime2";
        String value3 = "differentValueEveryTime3";
        when(tokenGenerator.generateToken()).thenReturn(value1).thenReturn(value2).thenReturn(value3);
        when(userCreationRepository.findOneByEmailToken(value1)).thenReturn(Optional.of(mock(UserCreation.class)));
        when(userCreationRepository.findOneByEmailToken(value2)).thenReturn(Optional.of(mock(UserCreation.class)));
        when(userCreationRepository.findOneByEmailToken(value3)).thenReturn(Optional.of(mock(UserCreation.class)));
        thrown.expect(UniqueValueGeneratorException.class);

        // Act
        sut.generateEmailToken();

        // Assert
        verify(tokenGenerator, times(3)).generateToken();
        verify(userCreationRepository, times(1)).findOneByEmailToken(value1);
        verify(userCreationRepository, times(1)).findOneByEmailToken(value2);
        verify(userCreationRepository, times(1)).findOneByEmailToken(value3);
    }
}