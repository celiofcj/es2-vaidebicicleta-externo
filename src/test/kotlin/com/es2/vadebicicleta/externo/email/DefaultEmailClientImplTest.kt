package com.es2.vadebicicleta.externo.email

import com.es2.vadebicicleta.externo.dominio.RequisicaoEmail
import com.es2.vadebicicleta.externo.email.client.DefaultEmailClientImpl
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage

class DefaultEmailClientImplTest {

    private val mockMailSender: MailSender = mockk(relaxed = true)
    private val enderecoOrigem = "origem@example.com"

    private val emailClient = DefaultEmailClientImpl(
        emailSender = mockMailSender,
        enderecoOrigem = enderecoOrigem
    )

    @Test
    fun testEnviarEmail() {
        val requisicaoEmail = RequisicaoEmail(
            email = "destino@example.com",
            assunto = "Assunto do email",
            mensagem = "Mensagem do email"
        )

        emailClient.enviarEmail(requisicaoEmail)

        verify {
            mockMailSender.send(withArg<SimpleMailMessage> { message ->
                assert(message.from == enderecoOrigem)
                assert(message.to?.size == 1 && message.to?.get(0) == requisicaoEmail.email)
                assert(message.subject == requisicaoEmail.assunto)
                assert(message.text == requisicaoEmail.mensagem)
            })
        }
    }
}