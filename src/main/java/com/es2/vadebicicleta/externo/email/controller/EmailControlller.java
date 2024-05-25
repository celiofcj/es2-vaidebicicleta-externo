package com.es2.vadebicicleta.externo.email.controller;

import com.es2.vadebicicleta.externo.email.dto.RequisicaoEmailConverter;
import com.es2.vadebicicleta.externo.email.dto.RequisicaoEmailInDto;
import com.es2.vadebicicleta.externo.email.model.RequisicaoEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class EmailControlller {
    private final RequisicaoEmailConverter requisicaoEmailConverter;

    @Autowired
    public EmailControlller(RequisicaoEmailConverter requisicaoEmailConverter) {
        this.requisicaoEmailConverter = requisicaoEmailConverter;
    }

    @PostMapping("/enviarEmail")
    public ResponseEntity<Object> enviarEmail(@RequestBody RequisicaoEmailInDto dto){

    }
}
