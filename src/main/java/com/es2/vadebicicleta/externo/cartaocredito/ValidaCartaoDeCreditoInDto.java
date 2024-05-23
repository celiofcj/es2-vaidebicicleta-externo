package com.es2.vadebicicleta.externo.cartaocredito;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidaCartaoDeCreditoInDto {
    private String nomeTitular;
    private String numero;
    private String validade;
    private String cvv;
}
