package com.backend.Config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ScheduleCategoryConverter
        implements AttributeConverter<GlobalEnum.ScheduleCategory, Integer> {

    @Override
    public Integer convertToDatabaseColumn(GlobalEnum.ScheduleCategory attribute) {
        return attribute == null ? null : attribute.getCode();
    }

    @Override
    public GlobalEnum.ScheduleCategory convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : GlobalEnum.ScheduleCategory.fromCode(dbData);
    }
}