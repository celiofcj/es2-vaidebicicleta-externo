package com.es2.vadebicicleta.externo.commons

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler
    fun handleAllUncaughtExceptions(ex: Exception) : ResponseEntity<MensagemErro>{
        val mensagemErro =  MensagemErro("100", "Um erro inesperado aconteceu")
        return ResponseEntity.internalServerError().body(mensagemErro)
    }
}

data class MensagemErro(
    val codigo: String,
    val mensagem: String
)