package com.es2.vadebicicleta.externo.email;

import com.es2.vadebicicleta.externo.email.model.RequisicaoEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class EmailService {
    private static List<RequisicaoEmail> emails;
    private static Integer autoIncrementCounter = 0;

    private final JavaMailSender emailSender;

    @Autowired
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public RequisicaoEmail enviarEmail(RequisicaoEmail requisicaoEmail){
        if(!Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", requisicaoEmail.getEmail())){
            throw new RuntimeException();
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("nao-responda@vaidebicicleta.com");
        message.setTo(requisicaoEmail.getEmail());
        message.setSubject(requisicaoEmail.getAssunto());
        message.setText(requisicaoEmail.getMensagem());

        emailSender.send(message);
        RequisicaoEmail emailEnviado = RequisicaoEmail.builder()
                .id(autoIncrementCounter++)
                .email(requisicaoEmail.getEmail())
                .assunto(requisicaoEmail.getAssunto())
                .mensagem(requisicaoEmail.getMensagem())
                .build();

        emails.add(emailEnviado);
        return emailEnviado;
    }
}
