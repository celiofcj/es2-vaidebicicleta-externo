package com.es2.vadebicicleta.externo.cartaocredito.controller

import com.es2.vadebicicleta.externo.cartaocredito.controller.dto.CartaoDeCreditoInDto
import com.es2.vadebicicleta.externo.cartaocredito.controller.dto.CartaoDeCreditoOutDto
import com.es2.vadebicicleta.externo.cartaocredito.model.CartaoDeCredito
import com.es2.vadebicicleta.externo.cartaocredito.service.CartaoCreditoService
import com.es2.vadebicicleta.externo.commons.dto.DtoConverter
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class CartaoCreditoController (val cartaoCreditoService: CartaoCreditoService,
                               val cartaoDeCreditoConverter: DtoConverter<CartaoDeCredito, CartaoDeCreditoInDto, CartaoDeCreditoOutDto>) {

    @PostMapping("/validaCartaoDeCredito")
    fun validarCartaoCredito(@Valid @RequestBody dto: CartaoDeCreditoInDto): ResponseEntity<Unit> {
        val cartaoDeCredito = cartaoDeCreditoConverter.toObject(dto)
        cartaoCreditoService.validarCartaoDeCredito(cartaoDeCredito)

        return ResponseEntity.ok().build()
    }
}
