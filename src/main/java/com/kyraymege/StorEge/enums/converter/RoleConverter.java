package com.kyraymege.StorEge.enums.converter;

import com.kyraymege.StorEge.enums.Authority;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class RoleConverter implements AttributeConverter<Authority, String> {
    @Override
    public String convertToDatabaseColumn(Authority authority) {
        if(authority == null)
            return null;

        return authority.getValue();
    }

    @Override
    public Authority convertToEntityAttribute(String s) {
        if(s == null)
            return null;

        return Stream.of(Authority.values())
                .filter(c -> c.getValue().equals(s))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
