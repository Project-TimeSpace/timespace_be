package com.backend.ConfigEnum.Converter;

import com.backend.ConfigEnum.GlobalEnum.GroupCategory;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GroupCategoryConverter
        implements AttributeConverter<GroupCategory, Integer> {

    /** 엔티티 → DB (INT) */
    @Override
    public Integer convertToDatabaseColumn(GroupCategory attribute) {
        return attribute != null
                ? attribute.getCode()
                : null;
    }

    /** DB (INT) → 엔티티 */
    @Override
    public GroupCategory convertToEntityAttribute(Integer dbData) {
        return dbData != null
                ? GroupCategory.fromCode(dbData)
                : null;
    }
}
