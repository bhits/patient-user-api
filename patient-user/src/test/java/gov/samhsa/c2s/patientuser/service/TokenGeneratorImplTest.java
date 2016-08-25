package gov.samhsa.c2s.patientuser.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.util.StringUtils;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class TokenGeneratorImplTest {

    @InjectMocks
    private TokenGeneratorImpl sut;

    @Test
    public void testGenerateToken() throws Exception {
        // Act
        final String value1 = sut.generateToken();
        final String value2 = sut.generateToken();

        // Assert
        assertTrue(StringUtils.hasText(value1));
        assertTrue(StringUtils.hasText(value2));
        assertNotEquals(value1, value2);
    }

    @Test
    public void testGenerateToken1() throws Exception {
        // Act
        final int maxLength = 7;
        final String value1 = sut.generateToken(maxLength);
        final String value2 = sut.generateToken(maxLength);

        // Assert
        assertTrue(StringUtils.hasText(value1));
        assertTrue(StringUtils.hasText(value2));
        assertNotEquals(value1, value2);
        assertTrue(value1.length() <= maxLength);
        assertTrue(value2.length() <= maxLength);
    }
}