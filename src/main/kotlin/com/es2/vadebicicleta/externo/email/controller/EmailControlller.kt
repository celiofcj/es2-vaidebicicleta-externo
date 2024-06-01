package com.es2.vadebicicleta.externo.email.controller

import com.es2.vadebicicleta.externo.email.dto.RequisicaoEmailConverter
import com.es2.vadebicicleta.externo.email.dto.RequisicaoEmailInDto
import com.es2.vadebicicleta.externo.email.service.EmailService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class EmailControlller (
    private val requisicaoEmailConverter: RequisicaoEmailConverter,
    private val emailService: EmailService
) {
    @PostMapping("/enviarEmail")
    fun enviarEmail(@RequestBody dto: RequisicaoEmailInDto): ResponseEntity<Any> {
        val emailEnviado = emailService.enviarEmail(requisicaoEmailConverter.inDtoToOject(dto))
        return ResponseEntity.ok(emailEnviado)
    }
}
