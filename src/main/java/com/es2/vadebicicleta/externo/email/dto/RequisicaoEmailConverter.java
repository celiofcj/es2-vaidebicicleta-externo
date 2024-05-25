package com.es2.vadebicicleta.externo.email.dto;

import com.es2.vadebicicleta.externo.email.model.RequisicaoEmail;
import org.springframework.stereotype.Component;

@Component
public class RequisicaoEmailConverter {
    public RequisicaoEmail inDtoToOject(RequisicaoEmailInDto dto){
        return RequisicaoEmail.builder()
                .email(dto.getEmail())
                .assunto(dto.getAssunto())
                .mensagem(dto.getMensagem())
                .build();
    }

    public RequisicaoEmailInDto objectToInDto(RequisicaoEmail object){
        return RequisicaoEmailInDto.builder()
                .email(object.getEmail())
                .assunto(object.getAssunto())
                .mensagem(object.getMensagem())
                .build();
    }
}
