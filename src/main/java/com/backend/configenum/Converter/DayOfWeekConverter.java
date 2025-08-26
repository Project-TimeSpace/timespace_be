package com.backend.configenum.Converter;

import com.backend.configenum.GlobalEnum.DayOfWeek;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class DayOfWeekConverter implements AttributeConverter<DayOfWeek, Integer> {

    /** 엔티티 → DB (TINYINT) */
    @Override
    public Integer convertToDatabaseColumn(DayOfWeek attribute) {
        return attribute != null
                ? attribute.getValue()
                : null;
    }

    /** DB (TINYINT) → 엔티티 */
    @Override
    public DayOfWeek convertToEntityAttribute(Integer dbData) {
        return dbData != null
                ? DayOfWeek.fromValue(dbData)
                : null;
    }
}
