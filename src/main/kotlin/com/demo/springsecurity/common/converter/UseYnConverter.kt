package com.demo.springsecurity.common.converter

import com.demo.springsecurity.enumeration.UseYn
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class UseYnConverter: AttributeConverter<UseYn, String> {
    override fun convertToDatabaseColumn(useYn: UseYn?): String {
        if (useYn == null || !useYn.isUsable()) {
            return "0"
        }

        return "1"
    }

    override fun convertToEntityAttribute(dbData: String?): UseYn {
        if ("1".equals(dbData) || "Y".equals(dbData)) {
            return UseYn.Y
        }

        return UseYn.N
    }
}