package com.es2.vadebicicleta.externo.email.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequisicaoEmail {
    private Integer id;
    private String email;
    private String assunto;
    private String mensagem;
}
