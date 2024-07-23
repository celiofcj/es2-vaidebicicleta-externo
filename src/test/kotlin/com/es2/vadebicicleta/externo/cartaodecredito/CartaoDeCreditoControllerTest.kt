package com.es2.vadebicicleta.externo.cartaodecredito

import com.es2.vadebicicleta.externo.cartaocredito.controller.CartaoDeCreditoController
import com.es2.vadebicicleta.externo.cartaocredito.controller.dto.CartaoDeCreditoInDto
import com.es2.vadebicicleta.externo.cartaocredito.service.CartaoDeCreditoService
import com.es2.vadebicicleta.externo.commons.dto.DtoConverter
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.just
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.web.servlet.function.RequestPredicates.contentType

@ExtendWith(MockKExtension::class)
@WebMvcTest(CartaoDeCreditoController::class, DtoConverter::class)
class CartaoDeCreditoControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var mockCartaoDeCreditoService: CartaoDeCreditoService

    private val mapper = jacksonObjectMapper()

    @Test
    @DisplayName("Quando validar um cartão de crédito válido, retorna 200")
    fun quandoValidarCartaoDeCreditoValidoRetorna200() {
        val cartaoDto = CartaoDeCreditoInDto(
            nomeTitular = "João da Silva",
            numero = "1234567890123456",
            validade = "2025-12-31",
            cvv = "123"
        )

        every { mockCartaoDeCreditoService.validarCartaoDeCredito(any()) } just Runs

        mockMvc.post("/validaCartaoDeCredito") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(cartaoDto)
        }.andExpect {
            status { isOk() }
        }
    }

    @Disabled("Erro desconhecido")
    @Test
    @DisplayName("Quando enviar campos nulos ou inválidos, retorna 422 com uma lista de erros")
    fun quandoEnviarCamposNulosOuInvalidosRetorna422() {
        val cartaoDto = CartaoDeCreditoInDto(
            nomeTitular = null,
            numero = null,
            validade = null,
            cvv = null
        )

        mockMvc.post("/validaCartaoDeCredito") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(cartaoDto)
        }.andExpect {
            status { isUnprocessableEntity() }
            header { contentType(MediaType.APPLICATION_JSON) }
            MockMvcResultMatchers.jsonPath("$").isArray
            MockMvcResultMatchers.jsonPath("$").value(CoreMatchers.hasItem(
                mapOf(
                    "codigo" to "422",
                    "mensagem" to "nomeTitular: Não pode ser nulo nem vazio"
                )
            ))
            MockMvcResultMatchers.jsonPath("$").value(CoreMatchers.hasItem(
                mapOf(
                    "codigo" to "422",
                    "mensagem" to "numero: Não pode ser nulo nem vazio"
                )
            ))
            MockMvcResultMatchers.jsonPath("$").value(CoreMatchers.hasItem(
                mapOf(
                    "codigo" to "422",
                    "mensagem" to "validade: Deve ser no formato yyyy-MM-dd"
                )
            ))
            MockMvcResultMatchers.jsonPath("$").value(CoreMatchers.hasItem(
                mapOf(
                    "codigo" to "422",
                    "mensagem" to "cvv: Deve ser composto por 3 ou 4 números"
                )
            ))
        }
    }

    @Test
    @DisplayName("Quando enviar um cartão de crédito com formato inválido, retorna 422 com erros de validação")
    fun quandoEnviarCartaoDeCreditoComFormatoInvalidoRetorna422() {
        val cartaoDto = CartaoDeCreditoInDto(
            nomeTitular = "João da Silva",
            numero = "123ABCD5678901234",
            validade = "2025-12-31",
            cvv = "12345"
        )

        mockMvc.post("/validaCartaoDeCredito") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(cartaoDto)
        }.andExpect {
            status { isUnprocessableEntity() }
            header { contentType(MediaType.APPLICATION_JSON) }
            MockMvcResultMatchers.jsonPath("$").isArray()
            MockMvcResultMatchers.jsonPath("$").value(
                CoreMatchers.hasItem(
                mapOf(
                    "codigo" to "422",
                    "mensagem" to "numero: Deve ser numérico"
                )
            ))
            MockMvcResultMatchers.jsonPath("$").value(CoreMatchers.hasItem(
                mapOf(
                    "codigo" to "422",
                    "mensagem" to "cvv: Deve ser composto por 3 ou 4 números"
                )
            ))
        }
    }
}