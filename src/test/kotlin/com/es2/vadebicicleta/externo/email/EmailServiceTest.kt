package com.es2.vadebicicleta.externo.email

import com.es2.vadebicicleta.externo.email.client.EmailClient
import com.es2.vadebicicleta.externo.email.model.RequisicaoEmail
import com.es2.vadebicicleta.externo.email.repository.EmailRepository
import com.es2.vadebicicleta.externo.email.service.CouldNotSendEmailException
import com.es2.vadebicicleta.externo.email.service.EmailService
import com.es2.vadebicicleta.externo.email.service.WrongEmailAdressFormatException
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.mockk.verifyAll
import io.mockk.verifyOrder
import org.junit.experimental.theories.suppliers.TestedOn
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertNotNull

@ExtendWith(MockKExtension::class)
class EmailServiceTest {
    @MockK
    lateinit var emailRepositoryMock: EmailRepository
    @MockK
    lateinit var emailClientMock: EmailClient
    @InjectMockKs
    lateinit var emailService : EmailService

    @Test
    @DisplayName("Quando envio uma requisição informações válidas, envia o email e persiste a requisição")
    fun testeEnviarEmailSucesso() {
        val email = "email@email.com"
        val assunto = "Assunto do email"
        val mensagem = "Mensagem do email"
        val id = 47L
        val requisicao = RequisicaoEmail(email,
            assunto, mensagem)

        every { emailClientMock.enviarEmail(email, assunto, mensagem) } returns Unit
        every { emailRepositoryMock.save(requisicao) } returns RequisicaoEmail(email, assunto, mensagem, id)

        val retorno = emailService.enviarEmail(RequisicaoEmail(email, assunto, mensagem))
        assertNotNull(retorno, "Retorno não pode ser nulo")

        verify(exactly = 1) { emailClientMock.enviarEmail(email, assunto, mensagem)}
        verify(exactly = 1) { emailRepositoryMock.save(requisicao) }
        verifyOrder {
            emailClientMock.enviarEmail(email, assunto, mensagem)
            emailRepositoryMock.save(requisicao)
        }

        assertAll("Verificações do retorno",
            { assertEquals(retorno.email, email, "Endereço de email retornado deve ser igual ao informado na requisição") },
            { assertEquals(retorno.assunto, assunto, "Assunto retornado deve ser igual ao informado na requisição") },
            { assertEquals(retorno.mensagem, mensagem, "Mensagem retornado deve ser igual ao informado na requisição") },
            { assertEquals(retorno.id, id, "Id retornado deve ser igual ao id esperado (${id})") }
        )
    }

    @Test
    @DisplayName("Quando envio endereço de email invalido, lança exceção")
    fun testeEnviarEmailEnderecoInvalido() {
        val email = "email.com"
        val assunto = "Assunto do email"
        val mensagem = "Mensagem do email"

        assertThrows<WrongEmailAdressFormatException> ("A exceção deve ser lançada")
        { emailService.enviarEmail(RequisicaoEmail(email, assunto, mensagem)) }
    }

    @Test
    @DisplayName("Quando o envio do email falha, lança exceção")
    fun enviarEmailFalhaNoEnvio() {
        val email = "email@email.com"
        val assunto = "Assunto do email"
        val mensagem = "Mensagem do email"

        every { emailClientMock.enviarEmail(email, assunto, mensagem) } throws Exception("Exceção genérica")

        assertThrows<CouldNotSendEmailException> ("A exceção deve ser lançada")
        { emailService.enviarEmail(RequisicaoEmail(email, assunto, mensagem)) }

        verify { emailClientMock.enviarEmail(email, assunto, mensagem) }
    }
}