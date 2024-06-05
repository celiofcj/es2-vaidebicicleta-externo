package com.es2.vadebicicleta.externo.email

import com.es2.vadebicicleta.externo.commons.dto.DtoConverter
import com.es2.vadebicicleta.externo.email.controller.EmailControlller
import com.es2.vadebicicleta.externo.email.controller.dto.RequisicaoEmailInDto
import com.es2.vadebicicleta.externo.email.controller.dto.RequisicaoEmailOutDto
import com.es2.vadebicicleta.externo.email.model.RequisicaoEmail
import com.es2.vadebicicleta.externo.email.service.EmailService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@ExtendWith(MockKExtension::class)
@WebMvcTest(EmailControlller::class)
class EmailControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc
    @MockkBean
    lateinit var converter: DtoConverter<RequisicaoEmail, RequisicaoEmailInDto, RequisicaoEmailOutDto>
    @MockkBean
    lateinit var emailService: EmailService

    @Test
    fun testeEnviarEmailSucesso() {
        val email = "email@email.com"
        val assunto = "Assunto do email"
        val mensagem = "Mensagem do email"
        val id = 47L
        val requisicao = RequisicaoEmail(email, assunto, mensagem)

        val esperado = RequisicaoEmail(email, assunto, mensagem, id)

        every { emailService.enviarEmail(requisicao) } returns esperado
        every { converter.toObject(RequisicaoEmailInDto(email, assunto, mensagem)) } returns RequisicaoEmail(email, assunto, mensagem)
        every { converter.toDto(esperado) } returns RequisicaoEmailOutDto(id, email, assunto, mensagem)

        val mapper = jacksonObjectMapper()
        mockMvc.post("/enviarEmail") {
            contentType = MediaType.APPLICATION_JSON
            content = mapper.writeValueAsString(requisicao)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            content { json(mapper.writeValueAsString(esperado))  }
        }

        verify (exactly = 1) { emailService.enviarEmail(requisicao) }
    }
}