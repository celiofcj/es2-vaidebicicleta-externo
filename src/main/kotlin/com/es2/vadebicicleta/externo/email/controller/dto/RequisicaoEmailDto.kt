package com.es2.vadebicicleta.externo.email.controller.dto

import com.es2.vadebicicleta.externo.commons.dto.DtoConverter
import com.es2.vadebicicleta.externo.email.model.RequisicaoEmail
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import org.springframework.stereotype.Component

@Component
class RequisicaoEmailConverter : DtoConverter<RequisicaoEmail, RequisicaoEmailInDto, RequisicaoEmailOutDto> {
    override fun toObject(inDto: RequisicaoEmailInDto): RequisicaoEmail {
        val email = inDto.email ?: ""
        val assunto = inDto.assunto ?: ""
        val mensagem = inDto.mensagem ?: ""
        return RequisicaoEmail(email, assunto, mensagem)
    }

    override fun toDto(o: RequisicaoEmail): RequisicaoEmailOutDto {
        return RequisicaoEmailOutDto(o.id, o.email, o.assunto, o.mensagem)
    }
}

data class RequisicaoEmailInDto(
    @field: NotNull(message = "Campo email não deve ser nulo")
    @field: Email(message = "Formato de email inválido." +
            " Recomendado consultar RFC 3696 e a errata associada")
    val email: String? = null,
    val assunto: String? = null,
    @field: NotNull(message = "Campo mensagem não deve ser nulo")
    val mensagem: String? = null
)

data class RequisicaoEmailOutDto(
    val id: Long?,
    val email: String?,
    val assunto: String?,
    val mensagem: String?
)