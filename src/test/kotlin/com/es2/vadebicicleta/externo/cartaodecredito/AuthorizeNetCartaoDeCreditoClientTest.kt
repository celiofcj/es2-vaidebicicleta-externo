package com.es2.vadebicicleta.externo.cartaodecredito

import com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.AuthorizeNetConfig
import com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.AuthorizeNetJsonClient
import com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.response.*
import com.es2.vadebicicleta.externo.commons.exception.ExternalServiceException
import com.es2.vadebicicleta.externo.dominio.CartaoDeCredito
import com.es2.vadebicicleta.externo.dominio.Ciclista
import com.es2.vadebicicleta.externo.dominio.StatusPagamentoEnum
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class AuthorizeNetCartaoDeCreditoClientTest {

    @InjectMockKs
    private lateinit var authorizeNetClient: AuthorizeNetJsonClient

    @MockK
    private lateinit var authorizeNetConfig: AuthorizeNetConfig

    @MockK
    private lateinit var restTemplate: RestTemplate


    @BeforeEach
    internal fun setUp() {
        every { authorizeNetConfig.id } returns "1"
        every { authorizeNetConfig.key } returns "AAAA1"
        every { authorizeNetConfig.url } returns "url.com"
    }

    @Test
    @DisplayName("Quando a validação do cartão de crédito é bem-sucedida, então deve retornar CartaoDeCreditoValidacaoStatus verdadeiro")
    fun testValidarCartaoDeCreditoSuccess() {
        val cartaoDeCredito = CartaoDeCredito("Celio Celio","3088000000000017", LocalDate.of(2024, 12, 15), "123")
        val response = successoResponse()

        val url = authorizeNetConfig.url

        every { restTemplate.postForEntity(url, any(), CreateTransactionResponse::class.java) } returns ResponseEntity.ok(response)

        val status = authorizeNetClient.validarCartaoDeCredito(cartaoDeCredito)

        assertEquals(true, status.valido)
        assertEquals(emptyList(), status.erros)
    }

    @Test
    @DisplayName("Quando a validação do cartão de crédito falha por CVV, então deve retornar CartaoDeCreditoValidacaoStatus falso com erros")
    fun testValidarCartaoDeCreditoCVVInvalido() {
        val cartaoDeCredito = CartaoDeCredito("Celio Celio","3088000000000017", LocalDate.of(2024, 12, 15), "123")
        val response = responseCVVInvalido()

        val url = authorizeNetConfig.url

        every { restTemplate.postForEntity(url, any(), CreateTransactionResponse::class.java) } returns ResponseEntity.ok(response)

        val status = authorizeNetClient.validarCartaoDeCredito(cartaoDeCredito)

        assertEquals(false, status.valido)
        assertEquals(3, status.erros.size)
        assertTrue(status.erros.contains("Cartao de credito invalido"))
        assertTrue(status.erros.contains("CVV invalido. Authorize.Net"))
        assertTrue(status.erros.contains("Operacao mal-sucedida. Consulte a operadora para obter mais detalhes"))
    }

    @Test
    @DisplayName("Quando a validação do cartão de crédito falha por vencimento, então deve retornar CartaoDeCreditoValidacaoStatus falso com erro de vencimento")
    fun testValidarCartaoDeCreditoVencido() {
        val cartaoDeCredito = CartaoDeCredito("Celio Celio", "3088000000000017", LocalDate.of(2022, 12, 15), "123")
        val response = responseVencido()

        val url = authorizeNetConfig.url

        every { restTemplate.postForEntity(url, any(), CreateTransactionResponse::class.java) } returns ResponseEntity.ok(response)

        val status = authorizeNetClient.validarCartaoDeCredito(cartaoDeCredito)

        assertEquals(false, status.valido)
        assertEquals(2, status.erros.size)
        assertTrue(status.erros.contains("Cartao de credito venciado"))
        assertTrue(status.erros.contains("Cartao de credito invalido"))
    }

    @Test
    @DisplayName("Quando a cobrança é bem-sucedida, então deve retornar CartaoDeCreditoCobrancaStatus com status PAGA")
    fun testEnviarCobrancaSuccess() {
        val valor = BigDecimal.valueOf(100.00)
        val cartaoDeCredito = CartaoDeCredito("Celio Celio","3088000000000017", LocalDate.of(2024, 12, 15), "123")
        val ciclista = Ciclista(
            1, "ATIVO", "Ciclista Teste",
            LocalDate.of(2001, 4, 1), nacionalidade = "BRASILEIRO", "email@email.com",
            "", "11111111111")
        val response = successoResponse()

        val url = authorizeNetConfig.url

        every { restTemplate.postForEntity(url, any(), CreateTransactionResponse::class.java) } returns ResponseEntity.ok(response)

        val status = authorizeNetClient.enviarCobranca(valor, cartaoDeCredito, ciclista)

        assertEquals(StatusPagamentoEnum.PAGA, status.status)
    }

    @Test
    @DisplayName("Quando ocorre uma falha na cobrança, então deve retornar CartaoDeCreditoCobrancaStatus com status FALHA e erros")
    fun testEnviarCobrancaFailure() {
        val valor = BigDecimal.valueOf(100.00)
        val cartaoDeCredito = CartaoDeCredito("Celio Celio","3088000000000017", LocalDate.of(2024, 12, 15), "123")
        val ciclista = Ciclista(
            1, "ATIVO", "Ciclista Teste",
            LocalDate.of(2001, 4, 1), nacionalidade = "BRASILEIRO", "email@email.com",
            "", "11111111111")
        val response = responseCVVInvalido()

        val url = authorizeNetConfig.url

        every { restTemplate.postForEntity(url, any(), CreateTransactionResponse::class.java) } returns ResponseEntity.ok(response)

        val status = authorizeNetClient.enviarCobranca(valor, cartaoDeCredito, ciclista)

        assertEquals(StatusPagamentoEnum.FALHA, status.status)
        assertEquals(3, status.erros?.size)
        assertTrue(status.erros?.contains("Cartao de credito invalido") == true)
        assertTrue(status.erros?.contains("CVV invalido. Authorize.Net") == true)
        assertTrue(status.erros?.contains("Operacao mal-sucedida. Consulte a operadora para obter mais detalhes") == true)
    }

    @Test
    @DisplayName("Quando ocorre uma falha na cobrança devido a cartão de crédito vencido, então deve retornar CartaoDeCreditoCobrancaStatus com status FALHA e erro de vencimento")
    fun testEnviarCobrancaCartaoVencido() {
        val valor = BigDecimal.valueOf(100.00)
        val cartaoDeCredito = CartaoDeCredito("Celio Celio", "3088000000000017", LocalDate.of(2022, 12, 15), "123")
        val ciclista = Ciclista(
            1, "ATIVO", "Ciclista Teste",
            LocalDate.of(2001, 4, 1), nacionalidade = "BRASILEIRO", "email@email.com",
            "", "11111111111")
        val response = responseVencido()

        val url = authorizeNetConfig.url

        every { restTemplate.postForEntity(url, any(), CreateTransactionResponse::class.java) } returns ResponseEntity.ok(response)

        val status = authorizeNetClient.enviarCobranca(valor, cartaoDeCredito, ciclista)

        assertEquals(StatusPagamentoEnum.FALHA, status.status)
        assertEquals(2, status.erros?.size)
        assertTrue(status.erros?.contains("Cartao de credito venciado") == true)
        assertTrue(status.erros?.contains("Cartao de credito invalido") == true)
    }


    private fun successoResponse() : CreateTransactionResponse {
        val transactionResponseMessages = listOf(Message("1", "This transaction has been approved."))
        val transactionResponse = TransactionResponse("1", "X7FZZP", "P", "M",
            "2", "80023495220", "", "", "0", "XXXX0017",
            "JCB", transactionResponseMessages, null, "", 3, "EJETE2JB2V7CISE40G4HYJM")

        val messages = Messages("Ok", listOf(MessagesDetail("I00001", "Successful")))

        return CreateTransactionResponse(transactionResponse, null, messages)
    }

    @Test
    @DisplayName("Quando o status da resposta não é OK, então deve lançar ExternalServiceException")
    fun testRequestLancaExceptionHttpStatusDiferenteDeOk() {
        val cartaoDeCredito = CartaoDeCredito("Celio Celio", "3088000000000017", LocalDate.of(2024, 12, 15), "123")
        val ciclista = Ciclista(
            1, "ATIVO", "Ciclista Teste",
            LocalDate.of(2001, 4, 1), nacionalidade = "BRASILEIRO", "email@email.com",
            "", "11111111111")
        val valor = BigDecimal.valueOf(100.00)
        val responseEntity = ResponseEntity<CreateTransactionResponse>(null, HttpStatus.INTERNAL_SERVER_ERROR)

        val url = authorizeNetConfig.url

        every { restTemplate.postForEntity(url, any(), CreateTransactionResponse::class.java) } returns responseEntity

        val exception = assertThrows<ExternalServiceException> {
            authorizeNetClient.enviarCobranca(valor, cartaoDeCredito, ciclista)
        }

        assertTrue(exception.message?.contains("HTTP Status code inesperado: 500") == true)
    }

    @Test
    @DisplayName("Quando o corpo da resposta é nulo, então deve lançar ExternalServiceException")
    fun testRequestLancaExceptionResponseBodyNulo() {
        val cartaoDeCredito = CartaoDeCredito("Celio Celio", "3088000000000017", LocalDate.of(2024, 12, 15), "123")
        val ciclista = Ciclista(
            1, "ATIVO", "Ciclista Teste",
            LocalDate.of(2001, 4, 1), nacionalidade = "BRASILEIRO", "email@email.com",
            "", "11111111111")
        val valor = BigDecimal.valueOf(100.00)
        val responseEntity = ResponseEntity<CreateTransactionResponse>(null, HttpStatus.OK)

        val url = authorizeNetConfig.url

        every { restTemplate.postForEntity(url, any(), CreateTransactionResponse::class.java) } returns responseEntity

        val exception = assertThrows<ExternalServiceException> {
            authorizeNetClient.enviarCobranca(valor, cartaoDeCredito, ciclista)
        }

        assertEquals("Erro na integracao com Authorize.Net.", exception.message)
    }

    private fun responseCVVInvalido(): CreateTransactionResponse {
        val errors = listOf(ErrorDetail("65", "This transaction has been declined."))
        val transactionResponse = TransactionResponse("2", "X7FZZP", "Y", "N",
            "2", "80023496079", "80023496188", "", "0", "XXXX0017",
            "JCB", errors = errors, transHashSha2 = "", supplementalDataQualificationIndicator = 3, networkTransId = "P7A6Z9TBV8O54800E1MT9GF")
        val messages = Messages("Ok", listOf(MessagesDetail("I00001", "Successful")))

        return CreateTransactionResponse(transactionResponse, null, messages)
    }

    private fun responseVencido() : CreateTransactionResponse {
        val errors = listOf(ErrorDetail("8", "The credit card has expired"))
        val transactionResponse = TransactionResponse("3", "", "P", "", "",
            "0", "", "", "0", "XXXX0017", "JCB", errors = errors,
            transHashSha2 = "", supplementalDataQualificationIndicator = 3)
        val messages = Messages("Error", listOf(MessagesDetail("E00027", "The transaction was unsuccessful.")))

        return CreateTransactionResponse(transactionResponse, null, messages)
    }
}


