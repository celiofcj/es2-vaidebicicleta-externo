package com.es2.vadebicicleta.externo.cobranca.controller

import com.es2.vadebicicleta.externo.cobranca.controller.dto.CobrancaInDto
import com.es2.vadebicicleta.externo.cobranca.controller.dto.CobrancaOutDto
import com.es2.vadebicicleta.externo.cobranca.model.Cobranca
import com.es2.vadebicicleta.externo.cobranca.service.CobrancaService
import com.es2.vadebicicleta.externo.commons.dto.DtoConverter
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cobranca")
class CobrancaController(val converter : DtoConverter<Cobranca, CobrancaInDto, CobrancaOutDto>, val cobrancaService: CobrancaService) {


    @PostMapping
    fun realizarCobranca(@Valid @RequestBody novaCobranca : CobrancaInDto) : ResponseEntity<CobrancaOutDto> {
        val cobranca = cobrancaService.enviarCobranca(converter.toObject(novaCobranca))
        val outDto = converter.toDto(cobranca)
        return ResponseEntity.ok(outDto)
    }
}