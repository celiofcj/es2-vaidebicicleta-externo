package com.es2.vadebicicleta.externo.cobranca

import com.es2.vadebicicleta.externo.cobranca.controller.CobrancaController
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.junit5.MockKExtension
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@ExtendWith(MockKExtension::class)
@WebMvcTest(CobrancaController::class)
class CobrancaControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    private val mapper = jacksonObjectMapper()

    @Test
    @DisplayName("Quando faço uma requisição válida, retorna 200 com a resposta esperada")
    @Disabled
    fun testeRealizarCobrancaSucesso() {
        val valor = 13.49F
        val idCiclista = 39L

        val id = 126L
        val status = "PAGA"
        val horaSolicitacao  = "2024-06-21T02:32:19.457Z"
        val horaFinalizacao = "2024-06-21T02:32:19.457Z"
        val valorResposta = 13.49F

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
    @Disabled
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
    @Disabled
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