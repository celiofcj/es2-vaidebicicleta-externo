package com.es2.vadebicicleta.externo.commons.dto

interface DtoConverter<T, InDto, OutDto> {
    fun toObject(inDto: InDto) : T
    fun toDto(o: T) : OutDto
}