package com.es2.vadebicicleta.externo.commons.exception

import com.es2.vadebicicleta.externo.cartaocredito.service.InvalidCreditCardException
import com.es2.vadebicicleta.externo.email.service.CouldNotSendEmailException
import com.es2.vadebicicleta.externo.email.service.WrongEmailAdressFormatException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler
    fun handleAllUncaughtExceptions(ex: Exception) : ResponseEntity<MensagemErro>{
        val mensagemErro =  MensagemErro("500", "Um erro inesperado aconteceu")
        logger.error { "$ex  ${ex.message ?: ""} "}
        return ResponseEntity.internalServerError().body(mensagemErro)
    }

    @ExceptionHandler
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException) : ResponseEntity<Collection<MensagemErro>> {
        val bindingResult = ex.bindingResult
        val fieldErrors = bindingResult.fieldErrors

        val mensagensDeErro = fieldErrors.map {
            val campo = it.field
            val mensagem = it.defaultMessage
            val codigo = "422"
            MensagemErro(codigo, "$campo: $mensagem")
        }

        logger.error { ex.message }
        return ResponseEntity.unprocessableEntity().body(mensagensDeErro)
    }

    @ExceptionHandler
    fun handleWrongEmailAdressFormatException(ex: WrongEmailAdressFormatException) : ResponseEntity<Collection<MensagemErro>> {
        val codigo = "422"
        val mensagem = ex.message ?: ""
        val mensagensDeErro = listOf<MensagemErro>(MensagemErro(codigo, mensagem))

        logger.error { ex.message }
        return ResponseEntity.unprocessableEntity().body(mensagensDeErro)
    }

    @ExceptionHandler
    fun handleCouldNotSendEmailException(ex: CouldNotSendEmailException) : ResponseEntity<MensagemErro> {
        val codigo = "500"
        val mensagem = ex.message ?: ""
        val mensagemErro = MensagemErro(codigo, mensagem)

        logger.error { ex.message }
        return ResponseEntity.internalServerError().body(mensagemErro)
    }

    @ExceptionHandler
    fun handleInvalidCreditCardException(ex: InvalidCreditCardException) : ResponseEntity<Collection<MensagemErro>>{
        val codigo = "422"
        val mensagem = "Cartao de credito: ${ex.cartaoDeCredito} invalido. Erros: ${ex.errors}"
        val mensagensDeErro = ex.errors?.map { erro -> MensagemErro(codigo, erro) }

        logger.info { mensagem }

        return ResponseEntity.unprocessableEntity().body(mensagensDeErro)
    }

    @ExceptionHandler
    fun handleResourceNotFoundException(ex: ResourceNotFoundException) : ResponseEntity<MensagemErro> {
        val codigo = "404"
        val mensagem = ex.message

        val mensagemErro = MensagemErro(codigo, mensagem ?: "")

        logger.info { mensagem }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensagemErro)
    }
}

data class MensagemErro(
    val codigo: String,
    val mensagem: String
)