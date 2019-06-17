package com.seniortest.urlshortener.common;

import org.springframework.stereotype.Service;

import java.time.Instant;

public interface InstantProvider {
    default Instant now(){
        return Instant.now();
    }

    @Service
    class Impl implements InstantProvider { }
}
