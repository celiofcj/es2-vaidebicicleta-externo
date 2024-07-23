package com.es2.vadebicicleta.externo.commons


import com.es2.vadebicicleta.externo.cartaocredito.service.InvalidCreditCardException
import com.es2.vadebicicleta.externo.commons.exception.ExternalServiceException
import com.es2.vadebicicleta.externo.commons.exception.GlobalExceptionHandler
import com.es2.vadebicicleta.externo.commons.exception.MensagemErro
import com.es2.vadebicicleta.externo.commons.exception.ResourceNotFoundException
import com.es2.vadebicicleta.externo.dominio.CartaoDeCredito
import org.junit.jupiter.api.Test
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import java.time.LocalDate

class GlobalExceptionHandlerTest {

    private val globalExceptionHandler = GlobalExceptionHandler()


    @Test
    fun testHandleAllUncaughtExceptions() {
        val exception = Exception("Erro inesperado")
        val responseEntity: ResponseEntity<MensagemErro> = globalExceptionHandler.handleAllUncaughtExceptions(exception)

        assert(responseEntity.statusCode == HttpStatus.INTERNAL_SERVER_ERROR)
        assert(responseEntity.body?.codigo == "500")
        assert(responseEntity.body?.mensagem == "Um erro inesperado aconteceu")
    }

    @Test
    fun testHandleMethodArgumentNotValidException() {
        val objeto = Any()
        val bindingResult = BeanPropertyBindingResult(objeto, "objeto")
        bindingResult.addError(FieldError("objeto", "campo1", "Erro no campo 1"))
        bindingResult.addError(FieldError("objeto", "campo2", "Erro no campo 2"))

        val methodParameter = MethodParameter(GlobalExceptionHandler::class.java.methods.first(), -1)
        val exception = MethodArgumentNotValidException(methodParameter, bindingResult)
        val responseEntity: ResponseEntity<Collection<MensagemErro>> = globalExceptionHandler.handleMethodArgumentNotValidException(exception)

        assert(responseEntity.statusCode == HttpStatus.UNPROCESSABLE_ENTITY)
        assert(responseEntity.body?.size == 2)
        assert(responseEntity.body?.contains(MensagemErro("422", "campo1: Erro no campo 1")) == true)
        assert(responseEntity.body?.contains(MensagemErro("422", "campo2: Erro no campo 2")) == true)
    }

    @Test
    fun testHandleExternalServiceException() {
        val exception = ExternalServiceException("Erro no serviço externo")
        val responseEntity: ResponseEntity<MensagemErro> = globalExceptionHandler.handleExternalServiceException(exception)

        assert(responseEntity.statusCode == HttpStatus.INTERNAL_SERVER_ERROR)
        assert(responseEntity.body?.codigo == "500")
        assert(responseEntity.body?.mensagem == "Erro no serviço externo")
    }

    @Test
    fun testHandleInvalidCreditCardException() {
        val errors = listOf("Erro 1", "Erro 2")
        val exception = InvalidCreditCardException(
            CartaoDeCredito("Ciclista Teste", "1234567890123456",
            LocalDate.of(2025, 3, 1), "123"), errors)
        val responseEntity: ResponseEntity<Collection<MensagemErro>> = globalExceptionHandler.handleInvalidCreditCardException(exception)

        assert(responseEntity.statusCode == HttpStatus.UNPROCESSABLE_ENTITY)
        assert(responseEntity.body?.size == 2)
        assert(responseEntity.body?.contains(MensagemErro("422", "Erro 1")) == true)
        assert(responseEntity.body?.contains(MensagemErro("422", "Erro 2")) == true)
    }

    @Test
    fun testHandleResourceNotFoundException() {
        val exception = ResourceNotFoundException("Recurso não encontrado")
        val responseEntity: ResponseEntity<MensagemErro> = globalExceptionHandler.handleResourceNotFoundException(exception)

        assert(responseEntity.statusCode == HttpStatus.NOT_FOUND)
        assert(responseEntity.body?.codigo == "404")
        assert(responseEntity.body?.mensagem == "Recurso não encontrado")
    }
}