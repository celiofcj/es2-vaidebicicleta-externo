package com.es2.vadebicicleta.externo.cartaocredito.controller.dto

import com.es2.vadebicicleta.externo.dominio.CartaoDeCredito
import com.es2.vadebicicleta.externo.commons.dto.DtoConverter
import com.es2.vadebicicleta.externo.commons.validation.DatePattern
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter


private const val FORMATO_DATA = "yyyy-MM-dd"

@Component
class CartaoDeCreditoConverter : DtoConverter<CartaoDeCredito, CartaoDeCreditoInDto, CartaoDeCreditoOutDto>{
    private val dateTimeFormatterDefault = DateTimeFormatter.ofPattern(FORMATO_DATA)

    override fun toObject(inDto: CartaoDeCreditoInDto) : CartaoDeCredito {
        val nomeTitular = inDto.nomeTitular ?: ""
        val assunto = inDto.numero ?: ""
        val validade = LocalDate.from(dateTimeFormatterDefault.parse(inDto.validade)) ?: LocalDate.EPOCH
        val cvv = inDto.cvv ?: ""

        return CartaoDeCredito(nomeTitular, assunto, validade, cvv)
    }

    override fun toDto(o: CartaoDeCredito): CartaoDeCreditoOutDto
        = CartaoDeCreditoOutDto(o.nomeTitular, o.numero, o.validade.format(dateTimeFormatterDefault), o.cvv)
}

data class CartaoDeCreditoInDto (
    @field: NotBlank(message = "Não pode ser nulo nem vazio")
    val nomeTitular: String?,
    @field: Pattern(regexp = "^\\d+$", message = "Deve ser numérico")
    @field: NotNull
    val numero: String?,
    @field: NotNull
    @field: DatePattern(message = "Deve ser no formato $FORMATO_DATA", pattern = FORMATO_DATA)
    val validade: String?,
    @field: NotNull
    @field: Pattern(regexp = "^\\d{3,4}$", message = "Deve ser composto por 3 ou 4 números")
    val cvv: String?
)

data class CartaoDeCreditoOutDto(
    val nomeTitular: String?,
    val numero: String?,
    val validade: String?,
    val cvv: String?
)