package com.es2.vadebicicleta.externo.cobranca.controller

import com.es2.vadebicicleta.externo.cobranca.controller.dto.CobrancatDto
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@RequestMapping
class CobrancaController {
    @PostMapping("/cobranca")
    fun realizarCobranca(@RequestBody novaCobranca : NovaCobrancaDto) : CobrancatDto {
        return CobrancatDto()
    }
}