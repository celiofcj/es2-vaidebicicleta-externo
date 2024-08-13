package com.es2.vadebicicleta.externo.dto

import com.es2.vadebicicleta.externo.dominio.RequisicaoEmail
import com.es2.vadebicicleta.externo.email.controller.dto.RequisicaoEmailConverter
import com.es2.vadebicicleta.externo.email.controller.dto.RequisicaoEmailInDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RequisicaoEmailConverterTest {

    private val converter = RequisicaoEmailConverter()

    @Test
    @DisplayName("Quando os dados de entrada são válidos, então deve converter para RequisicaoEmail corretamente")
    fun testQuandoDadosValidosEntaoConverterParaRequisicaoEmail() {
        val inDto = RequisicaoEmailInDto(
            email = "exemplo@dominio.com",
            assunto = "Assunto de Teste",
            mensagem = "Mensagem de Teste"
        )

        val requisicaoEmail = converter.toObject(inDto)

        assertEquals("exemplo@dominio.com", requisicaoEmail.email)
        assertEquals("Assunto de Teste", requisicaoEmail.assunto)
        assertEquals("Mensagem de Teste", requisicaoEmail.mensagem)
    }

    @Test
    @DisplayName("Quando os dados de saída são válidos, então deve converter para RequisicaoEmailOutDto corretamente")
    fun testQuandoDadosValidosEntaoConverterParaRequisicaoEmailOutDto() {
        val requisicaoEmail = RequisicaoEmail(
            id = 1L,
            email = "exemplo@dominio.com",
            assunto = "Assunto de Teste",
            mensagem = "Mensagem de Teste"
        )

        val outDto = converter.toDto(requisicaoEmail)

        assertEquals(1L, outDto.id)
        assertEquals("exemplo@dominio.com", outDto.email)
        assertEquals("Assunto de Teste", outDto.assunto)
        assertEquals("Mensagem de Teste", outDto.mensagem)
    }

    @Test
    @DisplayName("Quando campos opcionais são nulos, então deve converter corretamente com valores padrão")
    fun testQuandoCamposOpcionaisENulosEntaoConverterComValoresPadrao() {
        val inDto = RequisicaoEmailInDto(
            email = "exemplo@dominio.com",
            assunto = null,
            mensagem = "Mensagem de Teste"
        )

        val requisicaoEmail = converter.toObject(inDto)

        assertEquals("exemplo@dominio.com", requisicaoEmail.email)
        assertEquals("", requisicaoEmail.assunto) // Valor padrão é string vazia
        assertEquals("Mensagem de Teste", requisicaoEmail.mensagem)
    }
}
