package com.es2.vadebicicleta.externo.dto

import com.es2.vadebicicleta.externo.cobranca.controller.dto.CobrancaCoverter
import com.es2.vadebicicleta.externo.cobranca.controller.dto.CobrancaInDto
import com.es2.vadebicicleta.externo.commons.dto.UnsuportedConversionException
import com.es2.vadebicicleta.externo.dominio.Cobranca
import com.es2.vadebicicleta.externo.dominio.StatusPagamentoEnum
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CobrancaConverterTest {

    private val converter = CobrancaCoverter()

    @Test
    @DisplayName("Quando os dados de entrada são válidos, então deve converter para Cobranca corretamente")
    fun testQuandoDadosValidosEntaoConverterParaCobranca() {
        val inDto = CobrancaInDto(
            valor = BigDecimal("100.00"),
            ciclista = 1L
        )

        val cobranca = converter.toObject(inDto)

        assertEquals(BigDecimal("100.00"), cobranca.valor)
        assertEquals(1L, cobranca.ciclista)
    }

    @Test
    @DisplayName("Quando os dados de saída são válidos, então deve converter para CobrancaOutDto corretamente")
    fun testQuandoDadosValidosEntaoConverterParaCobrancaOutDto() {
        val cobranca = Cobranca(
            id = 1L,
            status = StatusPagamentoEnum.PAGA,
            horaSolicitacao = LocalDateTime.of(2024, 8, 13, 15, 30, 0, 0),
            horaFinalizacao = LocalDateTime.of(2024, 8, 13, 16, 0, 0, 0),
            valor = BigDecimal("150.00"),
            ciclista = 2L
        )

        val outDto = converter.toDto(cobranca)

        assertEquals(1L, outDto.id)
        assertEquals("PAGA", outDto.status)
        assertEquals("2024-08-13T15:30:00.000Z", outDto.horaSolicitacao)
        assertEquals("2024-08-13T16:00:00.000Z", outDto.horaFinalizacao)
        assertEquals(BigDecimal("150.00"), outDto.valor)
        assertEquals(2L, outDto.ciclista)
    }

    @Test
    @DisplayName("Quando valor ou ciclista são nulos, então deve lançar UnsuportedConversionException")
    fun testQuandoValorOuCiclistaNulosEntaoLancarUnsuportedConversionException() {
        val inDto = CobrancaInDto(
            valor = null,
            ciclista = 1L
        )

        assertThrows(UnsuportedConversionException::class.java) {
            converter.toObject(inDto)
        }

        val inDto2 = CobrancaInDto(
            valor = BigDecimal("50.00"),
            ciclista = null
        )

        assertThrows(UnsuportedConversionException::class.java) {
            converter.toObject(inDto2)
        }
    }

    @Test
    @DisplayName("Quando horaSolicitacao ou horaFinalizacao são nulas, então deve converter para CobrancaOutDto com valores nulos")
    fun testQuandoHoraSolicitacaoOuHoraFinalizacaoNulasEntaoConverterParaCobrancaOutDtoComValoresNulos() {
        val cobranca = Cobranca(
            id = 1L,
            status = StatusPagamentoEnum.PENDENTE,
            horaSolicitacao = null,
            horaFinalizacao = null,
            valor = BigDecimal("75.00"),
            ciclista = 3L
        )

        val outDto = converter.toDto(cobranca)

        assertEquals(1L, outDto.id)
        assertEquals("PENDENTE", outDto.status)
        assertEquals(null, outDto.horaSolicitacao)
        assertEquals(null, outDto.horaFinalizacao)
        assertEquals(BigDecimal("75.00"), outDto.valor)
        assertEquals(3L, outDto.ciclista)
    }
}
