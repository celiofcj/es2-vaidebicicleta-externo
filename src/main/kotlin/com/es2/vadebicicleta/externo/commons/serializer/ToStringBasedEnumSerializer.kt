package com.es2.vadebicicleta.externo.commons.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class ToStringBasedEnumSerializer <T : Enum<T>> : JsonSerializer<T>() {
    override fun serialize(value: T, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value.toString())
    }
}