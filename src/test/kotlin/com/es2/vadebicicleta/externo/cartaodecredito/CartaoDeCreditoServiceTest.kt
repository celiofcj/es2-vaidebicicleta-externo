package com.es2.vadebicicleta.externo.cartaodecredito

import com.es2.vadebicicleta.externo.cartaocredito.client.OperadoraCartaoDeCreditoClient
import com.es2.vadebicicleta.externo.cartaocredito.service.CartaoDeCreditoService
import com.es2.vadebicicleta.externo.cartaocredito.service.InvalidCreditCardException
import com.es2.vadebicicleta.externo.dominio.*
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class CartaoDeCreditoServiceTest {

    @MockK
    lateinit var operadoraCartaoDeCreditoClient: OperadoraCartaoDeCreditoClient

    @InjectMockKs
    lateinit var cartaoDeCreditoService: CartaoDeCreditoService

    @Test
    @DisplayName("Quando validar um cartão de crédito válido, não deve lançar exceção")
    fun quandoValidarUmCartaoDeCreditoValidoNaoDeveLancarExcecao() {
        val cartaoDeCredito = CartaoDeCredito("Ciclista Teste", "1234567890123456", LocalDate.of(2025, 3, 1), "123")
        val resposta = CartaoDeCreditoValidacaoStatus(true, emptyList())

        every { operadoraCartaoDeCreditoClient.validarCartaoDeCredito(cartaoDeCredito) } returns resposta

        cartaoDeCreditoService.validarCartaoDeCredito(cartaoDeCredito)

        verify(exactly = 1) { operadoraCartaoDeCreditoClient.validarCartaoDeCredito(cartaoDeCredito) }
    }

    @Test
    @DisplayName("Quando validar um cartão de crédito inválido, deve lançar InvalidCreditCardException")
    fun quandoValidarUmCartaoDeCreditoInvalidoDeveLancarInvalidCreditCardException() {
        val cartaoDeCredito = CartaoDeCredito("Ciclista Teste", "1234567890123456", LocalDate.of(2025, 3, 1), "123")
        val resposta = CartaoDeCreditoValidacaoStatus(false, listOf("Cartão expirado"))

        every { operadoraCartaoDeCreditoClient.validarCartaoDeCredito(cartaoDeCredito) } returns resposta

        val exception = assertThrows<InvalidCreditCardException> {
            cartaoDeCreditoService.validarCartaoDeCredito(cartaoDeCredito)
        }

        assertEquals(cartaoDeCredito, exception.cartaoDeCredito)
        assertEquals(resposta.erros, exception.errors)

        verify(exactly = 1) { operadoraCartaoDeCreditoClient.validarCartaoDeCredito(cartaoDeCredito) }
    }

    @Test
    @DisplayName("Quando enviar cobrança, deve retornar o status correto")
    fun quandoEnviarCobrancaDeveRetornarStatusCorreto() {
        val cartaoDeCredito = CartaoDeCredito("Ciclista Teste", "1234567890123456", LocalDate.of(2025, 3, 1), "123")
        val ciclista = Ciclista(1, "11111111111", null, "email@email.com")
        val valor = BigDecimal.TEN
        val cobrancaStatus = CartaoDeCreditoCobrancaStatus(StatusPagamentoEnum.PAGA)

        every { operadoraCartaoDeCreditoClient.enviarCobranca(valor, cartaoDeCredito, ciclista) } returns cobrancaStatus

        val resultado = cartaoDeCreditoService.enviarCobranca(valor, cartaoDeCredito, ciclista)

        assertEquals(cobrancaStatus, resultado)

        verify(exactly = 1) { operadoraCartaoDeCreditoClient.enviarCobranca(valor, cartaoDeCredito, ciclista) }
    }
}