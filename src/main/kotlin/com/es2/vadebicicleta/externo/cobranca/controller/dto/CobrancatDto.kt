package com.es2.vadebicicleta.externo.cobranca.controller.dto

import com.es2.vadebicicleta.externo.dominio.Cobranca
import com.es2.vadebicicleta.externo.commons.dto.DtoConverter
import com.es2.vadebicicleta.externo.commons.dto.UnsuportedConversionException
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.format.DateTimeFormatter

private const val FORMATO_DATA = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

@Component
class CobrancaCoverter : DtoConverter<Cobranca, CobrancaInDto, CobrancaOutDto> {
    private val dateTimeFormatterDefault = DateTimeFormatter.ofPattern(FORMATO_DATA)

    override fun toObject(inDto: CobrancaInDto): Cobranca {
        if(inDto.valor == null || inDto.ciclista == null) {
            throw UnsuportedConversionException("Não foi possível converter de CobrancaInDto para Cobranca");
        }

        val valor = inDto.valor.movePointRight(2).toLong()
        val ciclista = inDto.ciclista

        return Cobranca(ciclista, valor)
    }

    override fun toDto(o: Cobranca): CobrancaOutDto {
        return CobrancaOutDto(
            o.id,
            o.status.toString(),
            o.horaSolicitacao?.format(dateTimeFormatterDefault),
            o.horaFinalizacao?.format(dateTimeFormatterDefault),
            BigDecimal(o.valor).scaleByPowerOfTen(-2),
            o.ciclista
        )
    }

}

data class CobrancaInDto (
    @field: Digits(integer = 16,fraction = 2,
        message = "Campo valor aceita no máximo 16 casas de inteiros e 2 casas de precisão")
    @field: NotNull(message = "Campo valor não deve ser nulo")
    @field :Min(value = 0)
    val valor : BigDecimal?,
    @field: Min(0, message = "Campo ciclista não deve ser menor que 0.")
    @field:NotNull
    val ciclista : Long?
)

data class CobrancaOutDto (
    val id : Long? = null,
    val status : String? = null,
    val horaSolicitacao : String? = null,
    val horaFinalizacao : String? = null,
    val valor: BigDecimal? = null,
    val ciclista : Long? = null
)
