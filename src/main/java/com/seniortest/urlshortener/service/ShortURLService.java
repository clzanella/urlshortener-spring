package com.seniortest.urlshortener.service;

import com.seniortest.urlshortener.model.ShortURL;
import com.seniortest.urlshortener.repository.ShortURLRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ShortURLService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShortURLService.class);
    private static final String URL_REGEX = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.]{1}[a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?$";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);
    private static final long START_ID = Long.parseLong("lkjia", Character.MAX_RADIX);

    private final ShortURLRepository urlRepository;

    @Autowired
    public ShortURLService(ShortURLRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public Optional<ShortURL> findByStringID(String stringId){

        long id;

        try {
            id = stringToId(stringId);
        } catch (NumberFormatException exc){
            LOGGER.warn(String.format("Error parsing %s to long", stringId), exc);
            return Optional.empty();
        }

        return urlRepository.findById(id);
    }

    public Long stringToId(String stringId){
        return Long.parseLong(stringId, Character.MAX_RADIX) - START_ID;
    }

    public String idToString(long id){
        return Long.toString(id + START_ID, Character.MAX_RADIX);
    }

    public void save(@Valid ShortURL shortURL){
        urlRepository.save(shortURL);
    }

    public boolean validateURL(String url) {
        Matcher m = URL_PATTERN.matcher(url);
        return m.matches();
    }

}
