package com.backend.ConfigEnum.Converter;

import com.backend.ConfigEnum.GlobalEnum;
import com.backend.ConfigEnum.GlobalEnum.RequestStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RequestStatusConverter
        implements AttributeConverter<RequestStatus, Integer> {

    /** 엔티티 → DB (INT) */
    @Override
    public Integer convertToDatabaseColumn(GlobalEnum.RequestStatus attribute) {
        return attribute == null ? null : attribute.getCode(); // 1,2,3…
    }


    @Override
    public GlobalEnum.RequestStatus convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : GlobalEnum.RequestStatus.fromCode(dbData);
    }
}
