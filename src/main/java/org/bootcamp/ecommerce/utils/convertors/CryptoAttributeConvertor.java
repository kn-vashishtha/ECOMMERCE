package org.bootcamp.ecommerce.utils.convertors;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.bootcamp.ecommerce.utils.SimpleEncryptorUtil;
import org.springframework.stereotype.Component;

@Component("simpleAttributeConverter")
@Converter(autoApply = false)
@RequiredArgsConstructor
public class CryptoAttributeConvertor implements AttributeConverter<String, String> {
    private final SimpleEncryptorUtil simpleEncryptorUtil;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute == null ? null : simpleEncryptorUtil.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData == null ? null : simpleEncryptorUtil.decrypt(dbData);
    }
}
