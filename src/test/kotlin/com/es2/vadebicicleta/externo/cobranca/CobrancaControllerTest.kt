package com.es2.vadebicicleta.externo.cobranca

import com.es2.vadebicicleta.externo.dominio.StatusPagamentoEnum
import com.es2.vadebicicleta.externo.cobranca.controller.CobrancaController
import com.es2.vadebicicleta.externo.dominio.Cobranca
import com.es2.vadebicicleta.externo.cobranca.service.CobrancaService
import com.es2.vadebicicleta.externo.commons.dto.DtoConverter
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.junit5.MockKExtension
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@ExtendWith(MockKExtension::class)
@WebMvcTest(CobrancaController::class, DtoConverter::class, CobrancaService::class)
class CobrancaControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var mockCobrancaService: CobrancaService

    private val mapper = jacksonObjectMapper()

    private val formatoData = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    @Test
    @DisplayName("Quando faço uma requisição válida, retorna 200 com a resposta esperada")
    fun testeRealizarCobrancaSucesso() {

        val valor = 13.49F
        val valorLong = 1349L
        val idCiclista = 39L

        val id = 126L
        val status = "PAGA"
        val horaSolicitacao  = "2024-06-21T02:32:19.457Z"
        val horaSolicitacaoDateTime : LocalDateTime = LocalDateTime.from(DateTimeFormatter.ofPattern(formatoData).parse(horaSolicitacao))
        val horaFinalizacao = "2024-06-21T02:32:19.457Z"
        val horaFinalizacaoDateTime = LocalDateTime.from(DateTimeFormatter.ofPattern(formatoData).parse(horaSolicitacao))
        val valorResposta = 13.49F

        every { mockCobrancaService.enviarCobranca(Cobranca(idCiclista, 1349)) }
            .returns(
                Cobranca(idCiclista, 1349, id, status = StatusPagamentoEnum.PAGA,
                    horaSolicitacaoDateTime, horaFinalizacaoDateTime)
            )

        mockMvc.post("/cobranca") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(mapOf(
                "valor" to valor,
                "ciclista" to idCiclista
            ))
        }.andExpect {
            status { isOk() }
            header { MediaType.APPLICATION_JSON }
            jsonPath("$").value(equalTo(
                mapOf(
                    "id" to id,
                    "status" to status,
                    "horaSolicitacao" to horaSolicitacao,
                    "horrFinalizacao" to horaFinalizacao,
                    "valor" to valorResposta,
                    "ciclista" to idCiclista
                )
            ))
        }
    }

    @Test
    @DisplayName("Quando envio campos nulos não nuláveis, retorna 422 uma lista de erros com mensagem para cada campo")
    fun testeRealizarCobrancaCamposNulos() {
        val valor = null
        val idCiclista = null

        mockMvc.post("/cobranca") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(mapOf(
                "valor" to valor,
                "ciclista" to idCiclista
            ))
        }.andExpect {
            status { isUnprocessableEntity() }
            header { MediaType.APPLICATION_JSON }
            jsonPath("$").isArray
            jsonPath("$").value(CoreMatchers.hasItem(
                mapOf(
                    "codigo" to "422",
                    "mensagem" to "valor: Campo valor não deve ser nulo"
                )
            ))
            jsonPath("$").value(CoreMatchers.hasItem(
                mapOf(
                    "codigo" to "422",
                    "mensagem" to "idCicilista: Campo idCiclista não deve ser nulo"
                )
            ))
        }
    }

    @Test
    @DisplayName("Quando envio o valor negativo, retorna uma lista de erros com a mensagem de valor inválido")
    fun testeRealizarCobrancaValorNegativo() {
        val valor = -1F
        val idCiclista = 39L

        mockMvc.post("/cobranca") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(mapOf(
                "valor" to valor,
                "ciclista" to idCiclista
            ))
        }.andExpect {
            status { isUnprocessableEntity() }
            header { MediaType.APPLICATION_JSON }
            jsonPath("$").isArray
            jsonPath("$").value(
                CoreMatchers.hasItem(
                    mapOf(
                        "codigo" to "422",
                        "mensagem" to "valor: Campo valor não dever ser negativo"
                    )
                )
            )
        }
    }
}