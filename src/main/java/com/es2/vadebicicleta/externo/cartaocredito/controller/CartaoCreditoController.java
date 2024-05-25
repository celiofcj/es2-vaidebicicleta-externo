package com.es2.vadebicicleta.externo.cartaocredito.controller;

import com.es2.vadebicicleta.externo.cartaocredito.model.CartaoDeCredito;
import com.es2.vadebicicleta.externo.cartaocredito.model.MensagemErro;
import com.es2.vadebicicleta.externo.cartaocredito.service.CartaoCreditoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class CartaoCreditoController {
    private final CartaoCreditoService cartaoCreditoService;

    @Autowired
    public CartaoCreditoController(CartaoCreditoService cartaoCreditoService) {
        this.cartaoCreditoService = cartaoCreditoService;
    }

    @PostMapping("/validaCartaoDeCredito")
    public ResponseEntity<List<MensagemErro>> validarCartaoCredito(@RequestBody CartaoDeCredito cartaoDeCredito){
        List<MensagemErro> errosValidacao = cartaoCreditoService.validarCartaoDeCredito(cartaoDeCredito);

        if(errosValidacao.isEmpty()){
            return ResponseEntity.ok().build();
        }
        else{
            return ResponseEntity.unprocessableEntity().body(errosValidacao);
        }
    }

}
