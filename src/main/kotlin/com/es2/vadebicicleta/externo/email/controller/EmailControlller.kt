package com.es2.vadebicicleta.externo.email.controller

import com.es2.vadebicicleta.externo.commons.dto.DtoConverter
import com.es2.vadebicicleta.externo.email.controller.dto.RequisicaoEmailInDto
import com.es2.vadebicicleta.externo.email.controller.dto.RequisicaoEmailOutDto
import com.es2.vadebicicleta.externo.dominio.RequisicaoEmail
import com.es2.vadebicicleta.externo.email.service.EmailService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class EmailControlller (
    private val requisicaoEmailConverter: DtoConverter<RequisicaoEmail, RequisicaoEmailInDto, RequisicaoEmailOutDto>,
    private val emailService: EmailService
) {
    @PostMapping("/enviarEmail")
    fun enviarEmail(@Valid @RequestBody dto: RequisicaoEmailInDto): ResponseEntity<RequisicaoEmailOutDto> {
        val emailEnviado = emailService.enviarEmail(requisicaoEmailConverter.toObject(dto))
        val outDto = requisicaoEmailConverter.toDto(emailEnviado)
        return ResponseEntity.ok(outDto)
    }
}
