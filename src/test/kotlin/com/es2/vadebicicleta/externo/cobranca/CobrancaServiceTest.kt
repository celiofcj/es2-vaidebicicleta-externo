package com.es2.vadebicicleta.externo.cobranca

import com.es2.vadebicicleta.externo.cartaocredito.service.CartaoDeCreditoService
import com.es2.vadebicicleta.externo.cobranca.client.AluguelClient
import com.es2.vadebicicleta.externo.cobranca.repository.CobrancaRepository
import com.es2.vadebicicleta.externo.cobranca.service.CobrancaService
import com.es2.vadebicicleta.externo.commons.exception.ResourceNotFoundException
import com.es2.vadebicicleta.externo.dominio.*
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockKExtension::class)
class CobrancaServiceTest {

    @MockK
    lateinit var aluguelClient: AluguelClient

    @MockK
    lateinit var cartaoDeCreditoService: CartaoDeCreditoService

    @MockK
    lateinit var cobrancaRepository: CobrancaRepository

    @InjectMockKs
    lateinit var cobrancaService: CobrancaService

    @Test
    @DisplayName("Quando enviar uma cobrança com valores válidos, salva a cobrança")
    fun quandoEnviarUmaCobrancaComValoresValidosSalvaACobranca() {
        val novaCobranca = Cobranca(ciclista = 1, valor = BigDecimal.TEN)
        val ciclista = Ciclista(
            1, "ATIVO", "Ciclista Teste",
            LocalDate.of(2001, 4, 1), nacionalidade = "BRASILEIRO", "email@email.com",
            "", "11111111111"
        )
        val cartaoDeCredito = CartaoDeCredito(
            "Ciclista Teste", "1234567890123456",
            LocalDate.of(2025, 3, 1), "123"
        )
        val cobrancaResposta = CartaoDeCreditoCobrancaStatus(status = StatusPagamentoEnum.PAGA, erros = null)
        val cobrancaSalva = Cobranca(
            ciclista = 1, valor = BigDecimal.TEN, status = StatusPagamentoEnum.PAGA,
            horaSolicitacao = LocalDateTime.now(), horaFinalizacao = LocalDateTime.now()
        )

        every { aluguelClient.getCiclista(1) } returns ciclista
        every { aluguelClient.getCartaoDeCredito(1) } returns cartaoDeCredito
        every {
            cartaoDeCreditoService.enviarCobranca(
                BigDecimal.TEN,
                cartaoDeCredito,
                ciclista
            )
        } returns cobrancaResposta
        every { cobrancaRepository.save(any()) } returns cobrancaSalva

        val resultado = cobrancaService.enviarCobranca(novaCobranca)

        assertNotNull(resultado, "Resultado não pode ser nulo")
        verify(exactly = 1) { aluguelClient.getCiclista(1) }
        verify(exactly = 1) { aluguelClient.getCartaoDeCredito(1) }
        verify(exactly = 1) { cartaoDeCreditoService.enviarCobranca(BigDecimal.TEN, cartaoDeCredito, ciclista) }
        verify(exactly = 1) { cobrancaRepository.save(any<Cobranca>()) }

        assertAll("Verificações do resultado",
            { assertEquals(resultado.ciclista, novaCobranca.ciclista, "Id do ciclista deve ser igual ao informado") },
            { assertEquals(resultado.valor, novaCobranca.valor, "Valor deve ser igual ao informado") },
            { assertEquals(resultado.status, StatusPagamentoEnum.PAGA, "Status deve ser PAGA") },
            { assertNotNull(resultado.horaSolicitacao, "Hora de solicitação não pode ser nula") },
            { assertNotNull(resultado.horaFinalizacao, "Hora de finalização não pode ser nula") }
        )
    }

    @Test
    @DisplayName("Quando enviar uma cobrança com valor negativo, deve lançar exceção")
    fun quandoEnviarUmaCobrancaComValorNegativoDeveLancarExcecao() {
        val novaCobranca = Cobranca(ciclista = 1, valor = BigDecimal(-10))

        val exception = assertThrows<IllegalArgumentException> {
            cobrancaService.enviarCobranca(novaCobranca)
        }

        assertEquals("Valor não pode ser negativo", exception.message)
    }

    @Test
    @DisplayName("Quando obter uma cobrança por ID existente, deve retornar a cobrança")
    fun quandoObterUmaCobrancaPorIdExistenteDeveRetornarACobranca() {
        val cobranca = Cobranca(
            ciclista = 1, valor = BigDecimal.TEN, status = StatusPagamentoEnum.PAGA,
            horaSolicitacao = LocalDateTime.now(), horaFinalizacao = LocalDateTime.now()
        )

        every { cobrancaRepository.findById(1L) } returns Optional.of(cobranca)

        val resultado = cobrancaService.obterCobranca(1L)

        assertNotNull(resultado, "Resultado não pode ser nulo")
        verify(exactly = 1) { cobrancaRepository.findById(1L) }

        assertAll("Verificações do resultado",
            { assertEquals(resultado.ciclista, cobranca.ciclista, "Id do ciclista deve ser igual ao esperado") },
            { assertEquals(resultado.valor, cobranca.valor, "Valor deve ser igual ao esperado") },
            { assertEquals(resultado.status, cobranca.status, "Status deve ser igual ao esperado") },
            {
                assertEquals(
                    resultado.horaSolicitacao,
                    cobranca.horaSolicitacao,
                    "Hora de solicitação deve ser igual ao esperado"
                )
            },
            {
                assertEquals(
                    resultado.horaFinalizacao,
                    cobranca.horaFinalizacao,
                    "Hora de finalização deve ser igual ao esperado"
                )
            }
        )
    }

    @Test
    @DisplayName("Quando obter uma cobrança por ID inexistente, deve lançar exceção")
    fun quandoObterUmaCobrancaPorIdInexistenteDeveLancarExcecao() {
        every { cobrancaRepository.findById(1L) } returns Optional.empty()

        val exception = assertThrows<ResourceNotFoundException> {
            cobrancaService.obterCobranca(1L)
        }

        assertEquals("Cobrança não encontrada, id: 1", exception.message)
    }

    @Test
    @DisplayName("Quando colocar uma cobrança na fila, deve salvar com status PENDENTE")
    fun quandoColocarUmaCobrancaNaFilaDeveSalvarComStatusPendente() {
        val novaCobranca = Cobranca(ciclista = 1, valor = BigDecimal.TEN)
        val cobrancaSalva = Cobranca(
            ciclista = 1, valor = BigDecimal.TEN, status = StatusPagamentoEnum.PENDENTE,
            horaSolicitacao = LocalDateTime.now(), horaFinalizacao = LocalDateTime.now()
        )

        every { cobrancaRepository.save(any()) } returns cobrancaSalva

        val resultado = cobrancaService.colocarNaFilaDeCobranca(novaCobranca)

        assertNotNull(resultado, "Resultado não pode ser nulo")
        verify(exactly = 1) { cobrancaRepository.save(any<Cobranca>()) }

        assertAll("Verificações do resultado",
            { assertEquals(resultado.ciclista, novaCobranca.ciclista, "Id do ciclista deve ser igual ao informado") },
            { assertEquals(resultado.valor, novaCobranca.valor, "Valor deve ser igual ao informado") },
            { assertEquals(resultado.status, StatusPagamentoEnum.PENDENTE, "Status deve ser PENDENTE") },
            { assertNotNull(resultado.horaSolicitacao, "Hora de solicitação não pode ser nula") },
            { assertNotNull(resultado.horaFinalizacao, "Hora de finalização não pode ser nula") }
        )
    }

    @Test
    @DisplayName("Quando processar cobranças em fila, deve enviar e salvar as cobranças")
    fun quandoProcessarCobrancasEmFilaDeveEnviarESalvarAsCobrancas() {
        val cobrancaPendente = Cobranca(
            ciclista = 1, valor = BigDecimal.TEN, status = StatusPagamentoEnum.PENDENTE,
            horaSolicitacao = LocalDateTime.now(), horaFinalizacao = LocalDateTime.now()
        )
        val cobrancaSalva = Cobranca(
            ciclista = 1, valor = BigDecimal.TEN, status = StatusPagamentoEnum.PAGA,
            horaSolicitacao = LocalDateTime.now(), horaFinalizacao = LocalDateTime.now()
        )

        val cartaoDeCredito = CartaoDeCredito(
            "Ciclista Teste", "1234567890123456",
            LocalDate.of(2025, 3, 1), "123"
        )
        val ciclista = Ciclista(
            1, "ATIVO", "Ciclista Teste",
            LocalDate.of(2001, 4, 1), nacionalidade = "BRASILEIRO", "email@email.com",
            "", "11111111111"
        )

        every { cobrancaRepository.findByStatus(StatusPagamentoEnum.PENDENTE) } returns listOf(cobrancaPendente)
        every { aluguelClient.getCiclista(1) } returns ciclista
        every { aluguelClient.getCartaoDeCredito(1) } returns cartaoDeCredito
        every { cartaoDeCreditoService.enviarCobranca(BigDecimal.TEN, cartaoDeCredito, ciclista) } returns
                CartaoDeCreditoCobrancaStatus(status = StatusPagamentoEnum.PAGA, erros = null)
        every { cobrancaRepository.save(any()) } returns cobrancaSalva
    }
}