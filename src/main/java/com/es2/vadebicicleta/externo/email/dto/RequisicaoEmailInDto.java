package com.es2.vadebicicleta.externo.email.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequisicaoEmailInDto {
    private String email;
    private String assunto;
    private String mensagem;
}
