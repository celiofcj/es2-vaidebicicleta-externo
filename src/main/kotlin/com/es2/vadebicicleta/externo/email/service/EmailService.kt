package com.es2.vadebicicleta.externo.email.service

import com.es2.vadebicicleta.externo.email.model.RequisicaoEmail
import com.es2.vadebicicleta.externo.email.repository.EmailRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class EmailService @Autowired constructor(
    private val emailSender: JavaMailSender,
    private val repository: EmailRepository
) {
    @Value("\${spring.mail.username}")
    private val emailAddressOrigin: String? = null
    fun enviarEmail(requisicaoEmail: RequisicaoEmail): RequisicaoEmail {

        if (!Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", requisicaoEmail.email)) {
            throw WrongEmailAdressFormatException("The format of " + requisicaoEmail.email + "is wrong")
        }

        val message = SimpleMailMessage()
        message.from = emailAddressOrigin
        message.setTo(requisicaoEmail.email)
        message.subject = requisicaoEmail.assunto
        message.text = requisicaoEmail.mensagem
        emailSender.send(message)

        return repository.save(requisicaoEmail)
    }
}
