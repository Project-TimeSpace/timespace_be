package com.backend.ConfigEnum.Converter;

import com.backend.ConfigEnum.GlobalEnum.University;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UniversityConverter implements AttributeConverter<University, Integer> {
    @Override
    public Integer convertToDatabaseColumn(University university) {
        return university != null ? university.getCode() : null;
    }

    @Override
    public University convertToEntityAttribute(Integer dbData) {
        return dbData != null ? University.fromCode(dbData) : null;
    }
}

