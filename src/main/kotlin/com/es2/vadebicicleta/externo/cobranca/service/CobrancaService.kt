package com.es2.vadebicicleta.externo.cobranca.service

import com.es2.vadebicicleta.externo.cartaocredito.model.StatusPagamentoEnum
import com.es2.vadebicicleta.externo.cartaocredito.service.CartaoDeCreditoService
import com.es2.vadebicicleta.externo.cobranca.client.AluguelClient
import com.es2.vadebicicleta.externo.cobranca.model.Cobranca
import com.es2.vadebicicleta.externo.cobranca.repository.CobrancaRepository
import com.es2.vadebicicleta.externo.commons.exception.ResourceNotFoundException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDateTime

val logger = KotlinLogging.logger {}

@Service
class CobrancaService(
    val aluguelClient : AluguelClient,
    val cartaoDeCreditoService: CartaoDeCreditoService,
    val cobrancaRepository: CobrancaRepository
) {
    fun enviarCobranca(novaCobranca: Cobranca) : Cobranca {
        val horaSolicitacao = LocalDateTime.now();

        val valor : Long = when {
            novaCobranca.valor < 0 -> throw IllegalArgumentException("Valor não pode ser negativo")
            else -> novaCobranca.valor
        }

        val ciclista : Long = when {
            novaCobranca.ciclista < 0 -> throw IllegalArgumentException("Id do ciclista não pode ser negativo")
            else -> novaCobranca.ciclista
        }

        val cartaoDeCredito = aluguelClient.getCartaoDeCredito(ciclista)
            ?: throw BrokenRequirementException("Erro ao obter o cartão de crédito do ciclista: $ciclista")

        val statusPagamento = cartaoDeCreditoService.enviarCobranca(cartaoDeCredito)

        val horaFinalizacao = LocalDateTime.now()
        val cobranca = Cobranca(ciclista = ciclista, valor = valor,
            status = statusPagamento, horaSolicitacao = horaSolicitacao, horaFinalizacao = horaFinalizacao)

        return cobrancaRepository.save(cobranca)
    }

    fun obterCobranca(idCobranca: Long): Cobranca {
        return cobrancaRepository.findById(idCobranca).orElseThrow {
            ResourceNotFoundException("Cobrança não encontrada, id: $idCobranca") }
    }

    fun colocarNaFilaDeCobranca(novaCobranca: Cobranca): Cobranca {
        val horaSolicitacao = LocalDateTime.now();
        val horaFinalizacao = LocalDateTime.now()

        val cobranca =  Cobranca(
            ciclista = novaCobranca.ciclista, valor = novaCobranca.valor, status = StatusPagamentoEnum.PENDENTE,
            horaSolicitacao = horaSolicitacao, horaFinalizacao = horaFinalizacao, filaDeCobranca = true)

        return cobrancaRepository.save(cobranca)
    }
}