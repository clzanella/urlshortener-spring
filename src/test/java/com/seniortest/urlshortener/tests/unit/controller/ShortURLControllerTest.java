package com.seniortest.urlshortener.tests.unit.controller;


import com.seniortest.urlshortener.common.InstantProvicer;
import com.seniortest.urlshortener.controller.ShortURLController;
import com.seniortest.urlshortener.model.ShortURL;
import com.seniortest.urlshortener.service.ShortURLService;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ShortURLControllerTest {

    private static final String DURATION = "PT1H";

    @Test
    public void shortenUrlInvallidUrl() throws URISyntaxException {
        ShortURLService service = mock(ShortURLService.class);
        InstantProvicer instantProvicer = mock(InstantProvicer.class);
        ShortURLController controller = new ShortURLController(DURATION, service, instantProvicer);

        ShortURLController.ShortURLRequest request = new ShortURLController.ShortURLRequest();
        request.url = "xx";

        ResponseEntity<?> responseEntity = controller.shortenUrl(request, null);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, responseEntity.getStatusCode());
    }

    @Test
    public void shortenNakedUrl() throws URISyntaxException {

        final Instant instant = Instant.parse("2019-06-15T12:00:00.00Z");
        InstantProvicer instantProvicer = mock(InstantProvicer.class);
        doAnswer(invocation -> instant).when(instantProvicer).now();

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://shortulr.com/"));

        ShortURLService service = mock(ShortURLService.class);
        when(service.validateURL(isA(String.class))).thenReturn(true);
        when(service.idToString(isA(Long.class))).thenReturn("xxx");
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ((ShortURL)args[0]).setId(3L);
            return null;
        }).when(service).save(isA(ShortURL.class));

        ArgumentCaptor<ShortURL> argCaptor = ArgumentCaptor.forClass(ShortURL.class);

        ShortURLController controller = new ShortURLController(DURATION, service, instantProvicer);

        ShortURLController.ShortURLRequest urlRequest = new ShortURLController.ShortURLRequest();
        urlRequest.url = "google.com";

        ResponseEntity<?> responseEntity = controller.shortenUrl(urlRequest, request);

        verify(service, times(1)).save(argCaptor.capture());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(argCaptor.getValue().getUrl().startsWith("http"));
        assertTrue(argCaptor.getValue().getUrl().endsWith("google.com"));
        assertEquals(instant.plusSeconds(60*60).toEpochMilli(), argCaptor.getValue().getCreatedAt().plusSeconds(argCaptor.getValue().getExpireSeconds()).toEpochMilli());
    }

    @Test
    public void redirectUrlNotFound() {

        ShortURLService service = mock(ShortURLService.class);
        doAnswer(invocation -> Optional.empty()).when(service).findByStringID(isA(String.class));

        ShortURLController controller = new ShortURLController(DURATION, service, null);

        ResponseEntity<?> responseEntity = controller.redirectUrl("xx");

        ArgumentCaptor<String> argCaptor = ArgumentCaptor.forClass(String.class);
        verify(service, times(1)).findByStringID(argCaptor.capture());
        assertEquals("xx", argCaptor.getValue());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

    }

    @Test
    public void redirectUrlExpired() {

        InstantProvicer instantProvicer = mock(InstantProvicer.class);
        doAnswer(invocation -> Instant.parse("2019-06-15T14:00:00.00Z")).when(instantProvicer).now();

        ShortURLService service = mock(ShortURLService.class);
        doAnswer(invocation -> {

            ShortURL shortURL = new ShortURL();
            shortURL.setId(1L);
            shortURL.setUrl("google.com");
            shortURL.setCreatedAt(Instant.parse("2019-06-15T12:00:00.00Z"));

            return Optional.of(shortURL);

        }).when(service).findByStringID(isA(String.class));

        ShortURLController controller = new ShortURLController(DURATION, service, instantProvicer);

        ResponseEntity<?> responseEntity = controller.redirectUrl("xx");

        verify(service, times(1)).findByStringID(isA(String.class));
        assertEquals(HttpStatus.GONE, responseEntity.getStatusCode());

    }

    @Test
    public void redirectUrl() {

        InstantProvicer instantProvicer = mock(InstantProvicer.class);
        doAnswer(invocation -> Instant.parse("2019-06-15T10:00:00.00Z")).when(instantProvicer).now();

        ShortURLService service = mock(ShortURLService.class);
        doAnswer(invocation -> {

            ShortURL shortURL = new ShortURL();
            shortURL.setId(1L);
            shortURL.setUrl("google.com");
            shortURL.setCreatedAt(Instant.parse("2019-06-15T12:00:00.00Z"));

            return Optional.of(shortURL);

        }).when(service).findByStringID(isA(String.class));

        ShortURLController controller = new ShortURLController(DURATION, service, instantProvicer);

        ResponseEntity<?> responseEntity = controller.redirectUrl("xx");

        verify(service, times(1)).findByStringID(isA(String.class));
        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertEquals("google.com", responseEntity.getHeaders().getLocation().toString());
    }

}
