package com.es2.vadebicicleta.externo.integracao.cobranca.client

import com.es2.vadebicicleta.externo.cartaocredito.controller.dto.CartaoDeCreditoConverter
import com.es2.vadebicicleta.externo.cartaocredito.controller.dto.CartaoDeCreditoInDto
import com.es2.vadebicicleta.externo.cobranca.client.AluguelClient
import com.es2.vadebicicleta.externo.dominio.Ciclista
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.RestTemplate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Tag("integracao")
@SpringBootTest(classes = [AluguelClient::class, RestTemplate::class, CartaoDeCreditoConverter::class])
class AluguelClientTest {

    @Autowired
    lateinit var aluguelClient: AluguelClient

    @Test
    fun testGetCiclista() {
        val idCiclista = 1L
        val cpf = "78804034009"
        val passaporte = null
        val email = "user@example.com"


        val response = aluguelClient.getCiclista(1L)

        assertNotNull(response)
        assertEquals(idCiclista, response?.id)
        assertEquals(cpf, response?.cpf)
        assertEquals(passaporte, response?.passaporte)
        assertEquals(email, response?.email)
    }

    @Test
    fun testGetCartaoDeCredito() {
        val nome = "Fulano Beltrano"
        val numero = "4012001037141112"
        val validade = LocalDate.parse("2022-12-01", DateTimeFormatter.ISO_LOCAL_DATE)
        val cvv = "132"

        val response = aluguelClient.getCartaoDeCredito(1L)

        assertNotNull(response)
        assertEquals(response?.nomeTitular, nome)
        assertEquals(response?.numero, numero)
        assertEquals(response?.validade, validade)
        assertEquals(response?.cvv, cvv)
    }
}