package com.es2.vadebicicleta.externo.email.controller;

import com.es2.vadebicicleta.externo.email.dto.RequisicaoEmailConverter;
import com.es2.vadebicicleta.externo.email.dto.RequisicaoEmailInDto;
import com.es2.vadebicicleta.externo.email.model.RequisicaoEmail;
import com.es2.vadebicicleta.externo.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class EmailControlller {
    @Autowired
    private final RequisicaoEmailConverter requisicaoEmailConverter;
    @Autowired
    private EmailService emailService;


    @Autowired
    public EmailControlller(RequisicaoEmailConverter requisicaoEmailConverter) {
        this.requisicaoEmailConverter = requisicaoEmailConverter;
    }

    @PostMapping("/enviarEmail")
    public ResponseEntity<Object> enviarEmail(@RequestBody RequisicaoEmailInDto dto){
        RequisicaoEmail emailEnviado = emailService.enviarEmail(requisicaoEmailConverter.inDtoToOject(dto));

        return ResponseEntity.ok(emailEnviado);
    }
}
