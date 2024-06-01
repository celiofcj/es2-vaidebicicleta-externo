package com.es2.vadebicicleta.externo.email.dto

import com.es2.vadebicicleta.externo.commons.UnprocessableEntityException
import com.es2.vadebicicleta.externo.email.model.RequisicaoEmail
import org.springframework.stereotype.Component

@Component
class RequisicaoEmailConverter {
    fun inDtoToOject(dto: RequisicaoEmailInDto): RequisicaoEmail {
        if(dto.email == null || dto.assunto == null || dto.mensagem == null){
            throw UnprocessableEntityException()
        }
        return RequisicaoEmail(dto.email, dto.assunto, dto.mensagem)
    }

    fun objectToInDto(o: RequisicaoEmail): RequisicaoEmailInDto {
        return RequisicaoEmailInDto(o.email, o.assunto, o.mensagem)
    }
}
