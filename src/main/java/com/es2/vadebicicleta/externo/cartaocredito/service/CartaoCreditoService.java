package com.es2.vadebicicleta.externo.cartaocredito.service;

import com.es2.vadebicicleta.externo.cartaocredito.model.CartaoDeCredito;
import com.es2.vadebicicleta.externo.cartaocredito.model.MensagemErro;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class CartaoCreditoService {
    /*private final RestTemplate restTemplate;

    @Autowired
    public CartaoCreditoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }*/

    @Value(value = "${formato-data-validade}")
    private String formatoDataValidade;

    // TODO Adicionar requisição a operadora
    public List<MensagemErro> validarCartaoDeCredito(CartaoDeCredito cartaoDeCredito){
        List<MensagemErro> errosDeValidacao = new ArrayList<>();

        String nomeTitular = cartaoDeCredito.getNomeTitular();
        if(nomeTitular == null){
            MensagemErro mensagemErro = new MensagemErro("101", "Nome do titular não pode ser nulo");
            errosDeValidacao.add(mensagemErro);
        } else if(!Pattern.matches("^[A-Za-zÀ-ÿ\\s'-]+$",  nomeTitular)) {
            MensagemErro mensagemErro = new MensagemErro("102",
                    "Nome do titular deve conter apenas letras e espaços");
            errosDeValidacao.add(mensagemErro);
        }

        String numero = cartaoDeCredito.getNumero();
        if(numero == null){
            MensagemErro mensagemErro = new MensagemErro("201",
                    "Número do cartão de crédito não pode ser nulo");
            errosDeValidacao.add(mensagemErro);
        } else if(!Pattern.matches("^\\b(?:\\d[ -]*?){13,16}\\b$", numero)){
            MensagemErro mensagemErro = new MensagemErro("202",
                    "Número do cartão de crédito deve conter apenas números e ter 13 ou 16 dígitos");
            errosDeValidacao.add(mensagemErro);
        }

        String validade = cartaoDeCredito.getValidade();
        if(validade == null){
            MensagemErro mensagemErro = new MensagemErro("301",
                    "Data de validade não pode ser nulo");
            errosDeValidacao.add(mensagemErro);
        } else {
            try {
                LocalDate validadeLD = LocalDate.parse(validade, DateTimeFormatter.ofPattern(formatoDataValidade));
                if (validadeLD.isBefore(LocalDate.now())) {
                    MensagemErro mensagemErro = new MensagemErro("303",
                            "Data de validade inválida: cartão vencido");
                    errosDeValidacao.add(mensagemErro);
                }
            } catch (DateTimeParseException e) {
                MensagemErro mensagemErro = new MensagemErro("302",
                        "Data de validade inválida: formato inválido");
                errosDeValidacao.add(mensagemErro);
            }
        }

        String cvv = cartaoDeCredito.getCvv();
        if(cvv == null) {
            MensagemErro mensagemErro = new MensagemErro("401", "CVV não pode ser nulo");
            errosDeValidacao.add(mensagemErro);
            errosDeValidacao.add(mensagemErro);
        } else if(!Pattern.matches("^\\b\\d{3}\\b$", cvv)) {
            MensagemErro mensagemErro = new MensagemErro("402",
                    "CVV deve conter apenas números e deve ter 3 dígitos");
            errosDeValidacao.add(mensagemErro);
        }

        return errosDeValidacao;
    }
}
