package com.es2.vadebicicleta.externo.cobranca.controller.dto

import com.es2.vadebicicleta.externo.cobranca.model.NovaCobranca
import com.es2.vadebicicleta.externo.commons.dto.DtoConverter
import com.es2.vadebicicleta.externo.commons.dto.UnsuportedConversionException
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

class CobrancaCoverter : DtoConverter<NovaCobranca, CobrancaInDto, CobrancaOutDto> {
    override fun toObject(inDto: CobrancaInDto): NovaCobranca {
        if(inDto.valor == null || inDto.ciclista == null) {
            throw UnsuportedConversionException("");
        }

        val valor = inDto.valor.movePointRight(2).toLong()
        val ciclista = inDto.ciclista

        return NovaCobranca(valor, ciclista)
    }

    override fun toDto(o: NovaCobranca): CobrancaOutDto {
        TODO("Not yet implemented")
    }

}

data class CobrancaInDto (
    @Digits(integer = 16,fraction = 2,
        message = "Campo valor aceita no máximo 16 casas de inteiros e 2 casas de precisão")
    @NotNull(message = "Campo valor não deve ser nulo")
    val valor : BigDecimal?,
    @Min(0, message = "Campo ciclista não deve ser menor que 0.")
    @NotNull
    val ciclista : Long?
)

data class CobrancaOutDto (
    val id : Long?,
    val status : String?,
    val horaSolicitacao : String?,
    val horaFinalizacao : String?,
    val valor: BigDecimal?,
    val ciclista : Long?
)
