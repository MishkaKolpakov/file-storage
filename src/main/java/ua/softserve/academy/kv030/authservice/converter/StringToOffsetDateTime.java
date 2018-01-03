package ua.softserve.academy.kv030.authservice.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.OffsetDateTime;

/**
 * Created by Miha on 06.12.2017.
 */
public class StringToOffsetDateTime implements Converter<String, OffsetDateTime> {
    public OffsetDateTime convert(String source) {
        return OffsetDateTime.parse(source);
    }
}
