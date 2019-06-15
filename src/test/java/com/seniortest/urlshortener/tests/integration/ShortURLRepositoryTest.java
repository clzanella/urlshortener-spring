package com.seniortest.urlshortener.tests.integration;

import com.seniortest.urlshortener.Application;
import com.seniortest.urlshortener.model.ShortURL;
import com.seniortest.urlshortener.repository.ShortURLRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ShortURLRepositoryTest {

    @Autowired
    private ShortURLRepository urlRepository;

    @Test
    public void saveAndFind() {

        ShortURL entity = new ShortURL();

        Instant expiration = Instant.now().plusSeconds(60);

        entity.setUrl("google.com");
        entity.setCreatedAt(expiration);

        urlRepository.save(entity);
        Optional<ShortURL> foundEntity = urlRepository.findById(entity.getId());

        Assert.assertTrue(foundEntity.isPresent());
        Assert.assertEquals(entity.getUrl(), foundEntity.get().getUrl());
        Assert.assertEquals(entity.getCreatedAt(), foundEntity.get().getCreatedAt());
    }

}
