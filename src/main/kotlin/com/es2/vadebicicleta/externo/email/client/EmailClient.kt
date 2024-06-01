package com.es2.vadebicicleta.externo.email.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Component

interface EmailClient {
    fun enviarEmail(enderecoDestinatario: String, assunto: String, mensagem: String)
}

@Component
class DefaultEmailClientImpl (
    val emailSender: MailSender,
    @Value("\${spring.mail.username}")
    val enderecoOrigem: String? = null) : EmailClient {

    override fun enviarEmail(enderecoDestinatario: String, assunto: String, mensagem: String){
        val message = SimpleMailMessage()
        message.from = enderecoOrigem
        message.setTo(enderecoDestinatario)
        message.subject = assunto
        message.text = mensagem
        emailSender.send(message)
    }
}