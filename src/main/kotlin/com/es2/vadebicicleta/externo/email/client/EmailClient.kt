package com.es2.vadebicicleta.externo.email.client

import com.es2.vadebicicleta.externo.dominio.RequisicaoEmail
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Component

interface EmailClient {
    fun enviarEmail(requisicaoEmail: RequisicaoEmail)
}

@Component
class DefaultEmailClientImpl (
    val emailSender: MailSender,
    @Value("\${spring.mail.username}")
    val enderecoOrigem: String? = null) : EmailClient {

    override fun enviarEmail(requisicaoEmail: RequisicaoEmail){
        val message = SimpleMailMessage()
        message.from = enderecoOrigem
        message.setTo(requisicaoEmail.email)
        message.subject = requisicaoEmail.assunto
        message.text = requisicaoEmail.mensagem
        emailSender.send(message)
    }
}