package com.seniortest.urlshortener.tests.unit.service;

import com.seniortest.urlshortener.model.ShortURL;
import com.seniortest.urlshortener.repository.ShortURLRepository;
import com.seniortest.urlshortener.service.ShortURLService;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ShortURLServiceTest {

    @Test
    public void save(){
        ShortURLRepository repositoryMock = mock(ShortURLRepository.class);

        ShortURL entity = new ShortURL();
        ShortURLService service = new ShortURLService(repositoryMock);
        service.save(entity);

        ArgumentCaptor<ShortURL> argument = ArgumentCaptor.forClass(ShortURL.class);
        verify(repositoryMock, times(1)).save(argument.capture());
        assertEquals(entity, argument.getValue());
    }

    @Test
    public void find(){
        ShortURLRepository repositoryMock = mock(ShortURLRepository.class);

        ShortURL entity = new ShortURL();
        when(repositoryMock.findById(1L)).thenReturn(Optional.of(entity));

        ShortURLService service = new ShortURLService(repositoryMock);
        Optional<ShortURL> found = service.findByStringID("lkjib");

        assertTrue(found.isPresent());
        assertEquals(entity, found.get());

        ArgumentCaptor<Long> argument = ArgumentCaptor.forClass(Long.class);
        verify(repositoryMock, times(1)).findById(argument.capture());
        assertEquals(new Long(1), argument.getAllValues().get(0));
    }

    @Test
    public void findInvallidId(){
        ShortURLRepository repositoryMock = mock(ShortURLRepository.class);
        ShortURLService service = new ShortURLService(repositoryMock);

        Optional<ShortURL> found = service.findByStringID("##");

        assertFalse(found.isPresent());
    }

    @Test
    public void idToString(){
        ShortURLRepository repositoryMock = mock(ShortURLRepository.class);
        ShortURLService service = new ShortURLService(repositoryMock);

        String id1 = service.idToString(1);
        String id2 = service.idToString(15);
        String id3 = service.idToString(1500);

        assertEquals("lkjib", id1);
        assertEquals("lkjip", id2);
        assertEquals("lkkny", id3);
    }

    @Test
    public void validateUrl(){
        ShortURLRepository repositoryMock = mock(ShortURLRepository.class);
        ShortURLService service = new ShortURLService(repositoryMock);

        assertTrue(service.validateURL("google.com.br"));
        assertFalse(service.validateURL("localhost:8080"));
        assertTrue(service.validateURL("https://www.baeldung.com/spring-testing-separate-data-source"));
    }

}
