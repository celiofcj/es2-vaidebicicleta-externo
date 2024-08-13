package com.es2.vadebicicleta.externo.dto

import com.es2.vadebicicleta.externo.cartaocredito.controller.dto.CartaoDeCreditoConverter
import com.es2.vadebicicleta.externo.cartaocredito.controller.dto.CartaoDeCreditoInDto
import com.es2.vadebicicleta.externo.dominio.CartaoDeCredito
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.format.DateTimeParseException

class CartaoDeCreditoConverterTest {

    private val converter = CartaoDeCreditoConverter()

    @Test
    @DisplayName("Quando os dados de entrada são válidos, então deve converter para CartaoDeCredito corretamente")
    fun testQuandoDadosValidosEntaoConverterParaCartaoDeCredito() {
        val inDto = CartaoDeCreditoInDto(
            nomeTitular = "Maria Silva",
            numero = "1234567890123456",
            validade = "2024-12-31",
            cvv = "123"
        )

        val cartaoDeCredito = converter.toObject(inDto)

        assertEquals("Maria Silva", cartaoDeCredito.nomeTitular)
        assertEquals("1234567890123456", cartaoDeCredito.numero)
        assertEquals(LocalDate.of(2024, 12, 31), cartaoDeCredito.validade)
        assertEquals("123", cartaoDeCredito.cvv)
    }

    @Test
    @DisplayName("Quando os dados de saída são válidos, então deve converter para CartaoDeCreditoOutDto corretamente")
    fun testQuandoDadosValidosEntaoConverterParaCartaoDeCreditoOutDto() {
        val cartaoDeCredito = CartaoDeCredito(
            nomeTitular = "João Souza",
            numero = "6543210987654321",
            validade = LocalDate.of(2025, 11, 30),
            cvv = "456"
        )

        val outDto = converter.toDto(cartaoDeCredito)

        assertEquals("João Souza", outDto.nomeTitular)
        assertEquals("6543210987654321", outDto.numero)
        assertEquals("2025-11-30", outDto.validade)
        assertEquals("456", outDto.cvv)
    }

    @Test
    @DisplayName("Quando os dados de entrada são nulos ou vazios, então deve converter para valores padrão")
    fun testQuandoDadosNulosOuVaziosEntaoConverterParaValoresPadrao() {
        val inDto = CartaoDeCreditoInDto(
            nomeTitular = null,
            numero = null,
            validade = null,
            cvv = null
        )

        val cartaoDeCredito = converter.toObject(inDto)

        assertEquals("", cartaoDeCredito.nomeTitular)
        assertEquals("", cartaoDeCredito.numero)
        assertEquals(LocalDate.EPOCH, cartaoDeCredito.validade)
        assertEquals("", cartaoDeCredito.cvv)
    }
}