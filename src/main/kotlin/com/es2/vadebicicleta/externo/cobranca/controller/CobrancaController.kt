package com.es2.vadebicicleta.externo.cobranca.controller

import com.es2.vadebicicleta.externo.cobranca.controller.dto.CobrancaInDto
import com.es2.vadebicicleta.externo.cobranca.controller.dto.CobrancaOutDto
import com.es2.vadebicicleta.externo.dominio.Cobranca
import com.es2.vadebicicleta.externo.cobranca.service.CobrancaService
import com.es2.vadebicicleta.externo.commons.dto.DtoConverter
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping
class CobrancaController(val converter : DtoConverter<Cobranca, CobrancaInDto, CobrancaOutDto>, val cobrancaService: CobrancaService) {


    @PostMapping("/cobranca")
    fun realizarCobranca(@Valid @RequestBody novaCobranca : CobrancaInDto) : ResponseEntity<CobrancaOutDto> {
        val cobranca = cobrancaService.enviarCobranca(converter.toObject(novaCobranca))
        val outDto = converter.toDto(cobranca)
        return ResponseEntity.ok(outDto)
    }

    @GetMapping("cobranca/{idCobranca}")
    fun obterCobranca(@PathVariable idCobranca : Long) : ResponseEntity<CobrancaOutDto> {
        val cobranca = cobrancaService.obterCobranca(idCobranca)
        val outDto = converter.toDto(cobranca)

        return ResponseEntity.ok(outDto)
    }

    @PostMapping("/filaCobranca")
    fun colocarNaFilaDeCobranca(@Valid @RequestBody novaCobranca: CobrancaInDto) : ResponseEntity<CobrancaOutDto> {
        val cobranca = cobrancaService.colocarNaFilaDeCobranca(converter.toObject(novaCobranca))
        val outDto = converter.toDto(cobranca)
        return ResponseEntity.ok(outDto)
    }

    @PostMapping("/processaCobrancasEmFila")
    fun processaCobrancasEmFila() : ResponseEntity<List<CobrancaOutDto>> {
        val cobrancas = cobrancaService.processarCobrancasEmFila()

        val outDtos = cobrancas.map { cobranca -> converter.toDto(cobranca) }

        return ResponseEntity.ok(outDtos)
    }
}