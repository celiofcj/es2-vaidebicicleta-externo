package com.es2.vadebicicleta.externo.cobranca

import com.es2.vadebicicleta.externo.dominio.StatusPagamentoEnum
import com.es2.vadebicicleta.externo.cobranca.controller.CobrancaController
import com.es2.vadebicicleta.externo.cobranca.controller.dto.CobrancaInDto
import com.es2.vadebicicleta.externo.cobranca.controller.dto.CobrancaOutDto
import com.es2.vadebicicleta.externo.dominio.Cobranca
import com.es2.vadebicicleta.externo.cobranca.service.CobrancaService
import com.es2.vadebicicleta.externo.commons.dto.DtoConverter
import com.es2.vadebicicleta.externo.commons.exception.ResourceNotFoundException
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
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.math.BigDecimal
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
        val idCiclista = 39L

        val id = 126L
        val status = "PAGA"
        val horaSolicitacao  = "2024-06-21T02:32:19.457Z"
        val horaSolicitacaoDateTime : LocalDateTime = LocalDateTime.from(DateTimeFormatter.ofPattern(formatoData).parse(horaSolicitacao))
        val horaFinalizacao = "2024-06-21T02:32:19.457Z"
        val horaFinalizacaoDateTime = LocalDateTime.from(DateTimeFormatter.ofPattern(formatoData).parse(horaSolicitacao))
        val valorResposta = 13.49F

        every { mockCobrancaService.enviarCobranca(Cobranca(idCiclista, BigDecimal.valueOf(13.49))) }
            .returns(
                Cobranca(idCiclista, BigDecimal.valueOf(13.49), id, status = StatusPagamentoEnum.PAGA,
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

    @Test
    @DisplayName("Quando obter uma cobrança por ID existente, retorna 200 com a cobrança")
    fun testeObterCobrancaPorIdExistente() {
        val idCobranca = 1L
        val valor = BigDecimal.valueOf(50.00)
        val idCiclista = 42L
        val cobranca = Cobranca(
            ciclista = idCiclista,
            valor = valor,
            id = idCobranca,
            status = StatusPagamentoEnum.PAGA,
            horaSolicitacao = LocalDateTime.now(),
            horaFinalizacao = LocalDateTime.now()
        )

        every { mockCobrancaService.obterCobranca(idCobranca) } returns cobranca


        mockMvc.get("/cobranca/$idCobranca") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id").value(equalTo(idCobranca))
            jsonPath("$.status").value(equalTo(StatusPagamentoEnum.PAGA.name))
            jsonPath("$.valor").value(equalTo(valor))
            jsonPath("$.ciclista").value(equalTo(idCiclista))
        }
    }

    @Test
    @DisplayName("Quando obter uma cobrança por ID inexistente, retorna 404 com mensagem de não encontrado")
    fun testeObterCobrancaPorIdInexistente() {
        val idCobranca = 999L

        every { mockCobrancaService.obterCobranca(idCobranca) } throws ResourceNotFoundException("Cobrança não encontrada, id: $idCobranca")

        mockMvc.get("/cobranca/$idCobranca") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.mensagem").value(equalTo("Cobrança não encontrada, id: $idCobranca"))
        }
    }

    @Test
    @DisplayName("Quando colocar uma cobrança na fila, retorna 200 com a cobrança")
    fun testeColocarNaFilaDeCobranca() {
        val valor = BigDecimal.valueOf(25.75)
        val idCiclista = 5L
        val novaCobranca = CobrancaInDto(valor = valor, ciclista = idCiclista)

        val cobranca = Cobranca(
            ciclista = idCiclista,
            valor = valor,
            status = StatusPagamentoEnum.PENDENTE
        )

        every { mockCobrancaService.colocarNaFilaDeCobranca(any()) } returns cobranca

        mockMvc.post("/filaCobranca") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(novaCobranca)
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.status").value(equalTo(StatusPagamentoEnum.PENDENTE.name))
            jsonPath("$.valor").value(equalTo(valor))
            jsonPath("$.ciclista").value(equalTo(idCiclista))
        }
    }

    @Test
    @DisplayName("Quando processar cobranças em fila, retorna 200 com a lista de cobranças processadas")
    fun testeProcessarCobrancasEmFila() {
        val valor = BigDecimal.valueOf(30.00)
        val idCiclista = 7L
        val cobrancaProcessada = Cobranca(
            ciclista = idCiclista,
            valor = valor,
            status = StatusPagamentoEnum.PAGA
        )

        every { mockCobrancaService.processarCobrancasEmFila() } returns listOf(cobrancaProcessada)

        mockMvc.post("/processaCobrancasEmFila") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$[0].status").value(equalTo(StatusPagamentoEnum.PAGA.name))
            jsonPath("$[0].valor").value(equalTo(valor))
            jsonPath("$[0].ciclista").value(equalTo(idCiclista))
        }
    }
}