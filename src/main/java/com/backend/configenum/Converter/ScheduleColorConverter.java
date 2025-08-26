package com.backend.configenum.Converter;

import com.backend.configenum.GlobalEnum.ScheduleColor;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ScheduleColorConverter
        implements AttributeConverter<ScheduleColor, Integer> {

    /** 엔티티 → DB */
    @Override
    public Integer convertToDatabaseColumn(ScheduleColor attribute) {
        return attribute != null ? attribute.getCode() : null;
    }

    /** DB → 엔티티 */
    @Override
    public ScheduleColor convertToEntityAttribute(Integer dbData) {
        return dbData != null
                ? ScheduleColor.fromCode(dbData)
                : null;
    }
}
