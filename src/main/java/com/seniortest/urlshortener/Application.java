package com.seniortest.urlshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.time.DateTimeException;
import java.time.Duration;

@SpringBootApplication
public class Application {

    public static final String URL_EXPIRIRATION_DURATION_KEY = "app.urlExpiration";
    public static final String URL_EXPIRIRATION_DURATION_EXPR = "${" + URL_EXPIRIRATION_DURATION_KEY + "}";

    public static void main(final String[] args) {

        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);

        try {
            ConfigurableEnvironment env = ctx.getEnvironment();
            getUrlExpirationPeriod(env);
        } catch (Exception exc){
            ctx.close();
            exc.printStackTrace();
        }
    }

    public static Duration getUrlExpirationPeriod(Environment env){
        return getUrlExpirationPeriod(env.getProperty(URL_EXPIRIRATION_DURATION_KEY));
    }

    public static Duration getUrlExpirationPeriod(String propertyValue){

        if(propertyValue == null){
            throw new IllegalArgumentException();
        }

        try {
            return Duration.parse(propertyValue);
        } catch (DateTimeException exc){
            throw new RuntimeException(String.format("Invalid ISO-8601 Duration format %s for property %s.", propertyValue, URL_EXPIRIRATION_DURATION_KEY), exc);
        }
    }
}
