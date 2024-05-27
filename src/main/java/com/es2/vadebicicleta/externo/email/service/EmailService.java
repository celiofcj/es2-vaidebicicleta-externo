package com.es2.vadebicicleta.externo.email.service;

import com.es2.vadebicicleta.externo.email.model.RequisicaoEmail;
import com.es2.vadebicicleta.externo.email.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class EmailService {

    private final EmailRepository repository;
    private final JavaMailSender emailSender;

    @Autowired
    public EmailService(JavaMailSender emailSender, EmailRepository repository) {
        this.emailSender = emailSender;
        this.repository = repository;
    }

    public RequisicaoEmail enviarEmail(RequisicaoEmail requisicaoEmail){
        if(!Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", requisicaoEmail.getEmail())){
            throw new WrongEmailAdressFormatException("The format of " + requisicaoEmail.getEmail() + "is wrong");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("nao-responda@vaidebicicleta.com");
        message.setTo(requisicaoEmail.getEmail());
        message.setSubject(requisicaoEmail.getAssunto());
        message.setText(requisicaoEmail.getMensagem());

        //emailSender.send(message); FIXME Add when email is configured

        RequisicaoEmail emailEnviado = RequisicaoEmail.builder()
                .email(requisicaoEmail.getEmail())
                .assunto(requisicaoEmail.getAssunto())
                .mensagem(requisicaoEmail.getMensagem())
                .build();

        return repository.save(emailEnviado);
    }
}
