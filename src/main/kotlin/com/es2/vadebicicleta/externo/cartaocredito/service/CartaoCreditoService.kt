package com.es2.vadebicicleta.externo.cartaocredito.service

import com.es2.vadebicicleta.externo.cartaocredito.model.CartaoDeCredito
import com.es2.vadebicicleta.externo.cartaocredito.model.MensagemErro
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.regex.Pattern

@Service
class CartaoCreditoService {
    /*private final RestTemplate restTemplate;

    @Autowired
    public CartaoCreditoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }*/
    @Value(value = "\${formato-data-validade}")
    private val formatoDataValidade: String? = null

    // TODO Adicionar requisição a operadora
    fun validarCartaoDeCredito(cartaoDeCredito: CartaoDeCredito): List<MensagemErro> {
        val errosDeValidacao: MutableList<MensagemErro> = ArrayList()
        val nomeTitular = cartaoDeCredito.nomeTitular

        if (nomeTitular == null) {
            val mensagemErro = MensagemErro("101", "Nome do titular não pode ser nulo")
            errosDeValidacao.add(mensagemErro)
        } else if (!Pattern.matches("^[A-Za-zÀ-ÿ\\s'-]+$", nomeTitular)) {
            val mensagemErro = MensagemErro(
                "102",
                "Nome do titular deve conter apenas letras e espaços"
            )
            errosDeValidacao.add(mensagemErro)
        }
        val numero = cartaoDeCredito.numero
        if (numero == null) {
            val mensagemErro = MensagemErro(
                "201",
                "Número do cartão de crédito não pode ser nulo"
            )
            errosDeValidacao.add(mensagemErro)
        } else if (!Pattern.matches("^\\b(?:\\d[ -]*?){13,16}\\b$", numero)) {
            val mensagemErro = MensagemErro(
                "202",
                "Número do cartão de crédito deve conter apenas números e ter 13 ou 16 dígitos"
            )
            errosDeValidacao.add(mensagemErro)
        }
        val validade = cartaoDeCredito.validade
        if (validade == null) {
            val mensagemErro = MensagemErro(
                "301",
                "Data de validade não pode ser nulo"
            )
            errosDeValidacao.add(mensagemErro)
        } else {
            try {
                val validadeLD = LocalDate.parse(validade, DateTimeFormatter.ofPattern(formatoDataValidade))
                if (validadeLD.isBefore(LocalDate.now())) {
                    val mensagemErro = MensagemErro(
                        "303",
                        "Data de validade inválida: cartão vencido"
                    )
                    errosDeValidacao.add(mensagemErro)
                }
            } catch (e: DateTimeParseException) {
                val mensagemErro = MensagemErro(
                    "302",
                    "Data de validade inválida: formato inválido"
                )
                errosDeValidacao.add(mensagemErro)
            }
        }
        val cvv = cartaoDeCredito.cvv
        if (cvv == null) {
            val mensagemErro = MensagemErro("401", "CVV não pode ser nulo")
            errosDeValidacao.add(mensagemErro)
            errosDeValidacao.add(mensagemErro)
        } else if (!Pattern.matches("^\\b\\d{3}\\b$", cvv)) {
            val mensagemErro = MensagemErro(
                "402",
                "CVV deve conter apenas números e deve ter 3 dígitos"
            )
            errosDeValidacao.add(mensagemErro)
        }
        return errosDeValidacao
    }
}
