package com.es2.vadebicicleta.externo.cartaocredito;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class CartaoCreditoController {
    @PostMapping("/validaCartaoDeCredito")
    public ResponseEntity<?> validarCartaoCredito(@RequestBody ValidaCartaoDeCreditoInDto dto){
        return ResponseEntity.ok().build();
    }

}
