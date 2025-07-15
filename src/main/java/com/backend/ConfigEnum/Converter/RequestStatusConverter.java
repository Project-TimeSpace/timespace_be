package com.backend.ConfigEnum.Converter;

import com.backend.ConfigEnum.GlobalEnum.RequestStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RequestStatusConverter
        implements AttributeConverter<RequestStatus, Integer> {

    /** 엔티티 → DB (INT) */
    @Override
    public Integer convertToDatabaseColumn(RequestStatus attribute) {
        return attribute != null
                ? attribute.getCode()
                : null;
    }

    /** DB (INT) → 엔티티 */
    @Override
    public RequestStatus convertToEntityAttribute(Integer dbData) {
        return dbData != null
                ? RequestStatus.fromCode(dbData)
                : null;
    }
}
