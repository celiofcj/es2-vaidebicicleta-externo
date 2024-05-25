package com.es2.vadebicicleta.externo.cartaocredito.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartaoDeCredito {
    private String nomeTitular;
    private String numero;
    private String validade;
    private String cvv;
}
