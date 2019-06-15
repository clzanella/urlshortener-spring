package com.seniortest.urlshortener.repository;

import com.seniortest.urlshortener.model.ShortURL;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShortURLRepository extends CrudRepository<ShortURL, Long> {
}
