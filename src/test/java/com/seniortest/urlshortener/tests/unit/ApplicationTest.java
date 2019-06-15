package com.seniortest.urlshortener.tests.unit;

import com.seniortest.urlshortener.Application;
import org.junit.Test;
import org.springframework.core.env.Environment;

import java.time.Duration;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApplicationTest {

    @Test(expected = IllegalArgumentException.class)
    public void getWithhNullValue() {
        Environment envMock = mock(Environment.class);
        Application.getUrlExpirationPeriod(envMock);
    }

    @Test(expected = RuntimeException.class)
    public void getWrongValue() {
        Environment envMock = mock(Environment.class);
        when(envMock.getProperty(Application.URL_EXPIRIRATION_DURATION_KEY)).thenReturn("PT123");
        Application.getUrlExpirationPeriod(envMock);
    }

    @Test
    public void getValue() {

        Environment envMock = mock(Environment.class);
        when(envMock.getProperty(Application.URL_EXPIRIRATION_DURATION_KEY)).thenReturn("PT1H");

        Duration duration = Application.getUrlExpirationPeriod(envMock);
        assertEquals(duration.getSeconds(), 60 * 60);
    }
}
