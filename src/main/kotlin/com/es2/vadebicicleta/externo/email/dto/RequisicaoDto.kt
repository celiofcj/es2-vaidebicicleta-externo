package com.es2.vadebicicleta.externo.email.dto

import com.es2.vadebicicleta.externo.commons.dto.DtoConverter
import com.es2.vadebicicleta.externo.commons.UnprocessableEntityException
import com.es2.vadebicicleta.externo.email.model.RequisicaoEmail
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import org.springframework.stereotype.Component

@Component
class RequisicaoEmailConverter : DtoConverter<RequisicaoEmail, RequisicaoEmailInDto, RequisicaoEmailOutDto> {
    override fun toObject(inDto: RequisicaoEmailInDto): RequisicaoEmail {
        if(inDto.email == null || inDto.assunto == null || inDto.mensagem == null){
            throw UnprocessableEntityException()
        }
        return RequisicaoEmail(inDto.email, inDto.assunto, inDto.mensagem)
    }

    override fun toDto(o: RequisicaoEmail): RequisicaoEmailOutDto {
        return RequisicaoEmailOutDto(o.id, o.email, o.assunto, o.mensagem)
    }
}

data class RequisicaoEmailInDto(
    @field: NotNull(message = "Campo email não deve ser nulo")
    @field: Email(message = "Formato de email inválido")
    val email: String? = null,
    @field: NotNull(message = "Campo assunto não deve ser nulo")
    val assunto: String? = null,
    @field: NotNull(message = "Campo mensagem não deve ser nulo")
    val mensagem: String? = null
)

data class RequisicaoEmailOutDto(
    val id: Long? = null,
    val email: String? = null,
    val assunto: String? = null,
    val mensagem: String? = null
)