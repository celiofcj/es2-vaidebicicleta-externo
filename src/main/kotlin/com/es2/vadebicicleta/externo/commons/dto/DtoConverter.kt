package com.es2.vadebicicleta.externo.commons.dto

interface DtoConverter<T, InDto, OutDto> {
    fun toObject(inDto: InDto) : T
    fun toDto(o: T) : OutDto

    fun toObject(inDtoCollection: AbstractCollection<InDto>) = inDtoCollection.map { toObject(it) }
    fun toDto(oCollection : AbstractCollection<T>) = oCollection.map { toDto(it) }
}