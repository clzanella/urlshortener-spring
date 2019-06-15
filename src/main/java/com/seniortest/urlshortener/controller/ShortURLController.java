package com.seniortest.urlshortener.controller;

import com.seniortest.urlshortener.Application;
import com.seniortest.urlshortener.common.InstantProvicer;
import com.seniortest.urlshortener.model.ShortURL;
import com.seniortest.urlshortener.service.ShortURLService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Optional;

@RestController
public class ShortURLController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShortURLController.class);

    private final String expiration;
    private final ShortURLService urlService;
    private final InstantProvicer instantProvicer;

    @Autowired
    public ShortURLController(@Value(Application.URL_EXPIRIRATION_DURATION_EXPR) String expirationPeriod, ShortURLService urlService, InstantProvicer instantProvicer) {
        this.expiration = expirationPeriod;
        this.urlService = urlService;
        this.instantProvicer = instantProvicer;
    }

    @RequestMapping(value = "/shortener", method= RequestMethod.POST)
    public ResponseEntity<?> shortenUrl(@RequestBody final ShortURLController.ShortURLRequest shortenRequest, HttpServletRequest request) throws URISyntaxException {
        LOGGER.info("Received url to shorten: {}", shortenRequest.url);

        if(! urlService.validateURL(shortenRequest.url)){
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if(!shortenRequest.url.startsWith("http")){
            shortenRequest.url = "http://" + shortenRequest.url;
        }

        ShortURL shortURL = new ShortURL();
        shortURL.setUrl(shortenRequest.url);
        shortURL.setCreatedAt(instantProvicer.now());
        shortURL.setExpireSeconds(Duration.parse(this.expiration).getSeconds());

        urlService.save(shortURL);

        ShortURLResponse response = new ShortURLResponse();

        String shortId = urlService.idToString(shortURL.getId());
        URI uri = new URI(request.getRequestURL().toString()).resolve(shortId);
        response.newUrl = uri.toString();
        response.expiresAt = shortURL.getCreatedAt().plusSeconds(shortURL.getExpireSeconds()).toEpochMilli();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id:[a-z0-9]+}", method=RequestMethod.GET)
    public ResponseEntity<?> redirectUrl(@PathVariable String id) {
        LOGGER.info("Received shortened url to redirect: {}", id);

        Optional<ShortURL> entity = urlService.findByStringID(id);

        if(!entity.isPresent()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String redirectUrlString = entity.get().getUrl();
        LOGGER.info("Original URL. From {} to {}", id, redirectUrlString);

        if(instantProvicer.now().isAfter(entity.get().getCreatedAt().plusSeconds(entity.get().getExpireSeconds()))){
            LOGGER.info("Expired URL {}", id);
            return new ResponseEntity<>(HttpStatus.GONE);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(redirectUrlString));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    public static class ShortURLRequest {
        public String url;
    }

    public static class ShortURLResponse {
        public String newUrl;
        public long expiresAt;
    }

}
