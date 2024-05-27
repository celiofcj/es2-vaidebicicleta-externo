package com.es2.vadebicicleta.externo.email.model;

import com.es2.vadebicicleta.externo.commons.repository.Identificable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequisicaoEmail implements Identificable<Long> {
    private Long id;
    private String email;
    private String assunto;
    private String mensagem;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
