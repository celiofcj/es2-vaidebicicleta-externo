package com.es2.vadebicicleta.externo.cartaocredito.controller

import com.es2.vadebicicleta.externo.cartaocredito.model.CartaoDeCredito
import com.es2.vadebicicleta.externo.cartaocredito.model.MensagemErro
import com.es2.vadebicicleta.externo.cartaocredito.service.CartaoCreditoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class CartaoCreditoController @Autowired constructor(private val cartaoCreditoService: CartaoCreditoService) {
    @PostMapping("/validaCartaoDeCredito")
    fun validarCartaoCredito(@RequestBody cartaoDeCredito: CartaoDeCredito): ResponseEntity<List<MensagemErro?>?> {
        val errosValidacao = cartaoCreditoService.validarCartaoDeCredito(cartaoDeCredito)
        return if (errosValidacao.isEmpty()) {
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.unprocessableEntity().body(errosValidacao)
        }
    }
}
