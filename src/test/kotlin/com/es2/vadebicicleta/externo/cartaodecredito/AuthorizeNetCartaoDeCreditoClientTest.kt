//package com.es2.vadebicicleta.externo.cartaodecredito
//
//import com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.AuthorizeNetCartaoDeCreditoClient
//import com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.getTransationController
//import com.es2.vadebicicleta.externo.dominio.CartaoDeCredito
//import com.es2.vadebicicleta.externo.dominio.Ciclista
//import com.es2.vadebicicleta.externo.dominio.StatusPagamentoEnum
//import io.mockk.*
//import io.mockk.junit5.MockKExtension
//import net.authorize.Environment
//import net.authorize.api.contract.v1.CreateTransactionResponse
//import net.authorize.api.contract.v1.MerchantAuthenticationType
//import net.authorize.api.contract.v1.TransactionResponse
//import net.authorize.api.controller.CreateTransactionController
//import net.authorize.api.controller.base.ApiOperationBase
//import org.junit.jupiter.api.AfterEach
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.DisplayName
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.extension.ExtendWith
//import java.math.BigDecimal
//import java.time.LocalDate
//import kotlin.test.assertEquals
//
//@ExtendWith(MockKExtension::class)
//class AuthorizeNetCartaoDeCreditoClientTest {
//
//    private val createTransactionControllerMock = mockk<CreateTransactionController>(relaxed = true)
//
//    private lateinit var authorizeNetClient: AuthorizeNetCartaoDeCreditoClient
//
//    @BeforeEach
//    fun setUp() {
//        mockkStatic("com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.AuthrorizeNetUtils")
//        every { getTransationController(any()) } returns createTransactionControllerMock
//    }
//
//    @Test
//    @DisplayName("Quando a validação do cartão de crédito é bem-sucedida, então deve retornar CartaoDeCreditoValidacaoStatus verdadeiro")
//    fun testValidarCartaoDeCreditoSuccess() {
//        val cartaoDeCredito = CartaoDeCredito("Celio Celio","4111111111111111", LocalDate.of(2024, 12, 15), "123")
//        val response = CreateTransactionResponse().apply {
//            transactionResponse = TransactionResponse().apply { responseCode = "1" }
//        }
//
//
//        every { createTransactionControllerMock.execute() } returns Unit
//        every { createTransactionControllerMock.apiResponse } returns response
//
//        val status = authorizeNetClient.validarCartaoDeCredito(cartaoDeCredito)
//
//        assertEquals(true, status.valido)
//        assertEquals(emptyList(), status.erros)
//    }
//
//    @Test
//    @DisplayName("Quando a validação do cartão de crédito falha, então deve retornar CartaoDeCreditoValidacaoStatus falso com erros")
//    fun testValidarCartaoDeCreditoFailure() {
//        val cartaoDeCredito = CartaoDeCredito("Celio Celio","4111111111111111", LocalDate.of(2024, 12, 15), "123")
//        val response = CreateTransactionResponse().apply {
//            transactionResponse = TransactionResponse().apply { responseCode = "2" }
//        }
//
//        every { createTransactionControllerMock.execute() } returns Unit
//        every { createTransactionControllerMock.apiResponse } returns response
//
//        val status = authorizeNetClient.validarCartaoDeCredito(cartaoDeCredito)
//
//        assertEquals(false, status.valido)
//        assertEquals(listOf("Cartao de credito invalido"), status.erros)
//    }
//
//    @Test
//    @DisplayName("Quando a cobrança é bem-sucedida, então deve retornar CartaoDeCreditoCobrancaStatus com status PAGA")
//    fun testEnviarCobrancaSuccess() {
//        val valor = BigDecimal.valueOf(100.00)
//        val cartaoDeCredito = CartaoDeCredito("Celio Celio","4111111111111111", LocalDate.of(2024, 12, 15), "123")
//        val ciclista = Ciclista(
//            1, "ATIVO", "Ciclista Teste",
//            LocalDate.of(2001, 4, 1), nacionalidade = "BRASILEIRO", "email@email.com",
//            "", "11111111111")
//        val response = CreateTransactionResponse().apply {
//            transactionResponse = TransactionResponse().apply { responseCode = "1" }
//        }
//
//        every { createTransactionControllerMock.execute() } returns Unit
//        every { createTransactionControllerMock.apiResponse } returns response
//
//        val status = authorizeNetClient.enviarCobranca(valor, cartaoDeCredito, ciclista)
//
//        assertEquals(StatusPagamentoEnum.PAGA, status.status)
//    }
//
//    @Test
//    @DisplayName("Quando ocorre uma falha na cobrança, então deve retornar CartaoDeCreditoCobrancaStatus com status FALHA e erros")
//    fun testEnviarCobrancaFailure() {
//        val valor = BigDecimal.valueOf(100.00)
//        val cartaoDeCredito = CartaoDeCredito("Celio Celio","4111111111111111", LocalDate.of(2024, 12, 15), "123")
//        val ciclista = Ciclista(
//            1, "ATIVO", "Ciclista Teste",
//            LocalDate.of(2001, 4, 1), nacionalidade = "BRASILEIRO", "email@email.com",
//            "", "11111111111")
//        val response = CreateTransactionResponse().apply {
//            transactionResponse = TransactionResponse().apply { responseCode = "2" }
//        }
//
//        every { createTransactionControllerMock.execute() } returns Unit
//        every { createTransactionControllerMock.apiResponse } returns response
//
//        val status = authorizeNetClient.enviarCobranca(valor, cartaoDeCredito, ciclista)
//
//        assertEquals(StatusPagamentoEnum.FALHA, status.status)
//        assertEquals(listOf("Cartao de credito invalido"), status.erros)
//    }
//
//    @AfterEach
//    fun tearDown() {
//        unmockkStatic("com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.TransactionUtilsKt")
//    }
//}