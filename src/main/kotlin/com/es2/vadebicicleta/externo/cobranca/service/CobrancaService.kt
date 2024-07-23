package com.es2.vadebicicleta.externo.cobranca.service

import com.es2.vadebicicleta.externo.dominio.StatusPagamentoEnum
import com.es2.vadebicicleta.externo.cartaocredito.service.CartaoDeCreditoService
import com.es2.vadebicicleta.externo.cobranca.client.AluguelClient
import com.es2.vadebicicleta.externo.dominio.Cobranca
import com.es2.vadebicicleta.externo.cobranca.repository.CobrancaRepository
import com.es2.vadebicicleta.externo.commons.exception.ResourceNotFoundException
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime


@Service
class CobrancaService(
    val aluguelClient : AluguelClient,
    val cartaoDeCreditoService: CartaoDeCreditoService,
    val cobrancaRepository: CobrancaRepository
) {
    fun enviarCobranca(novaCobranca: Cobranca) : Cobranca {
        val horaSolicitacao = LocalDateTime.now()

        require(novaCobranca.valor >= BigDecimal.ZERO) { "Valor não pode ser negativo" }
        require(novaCobranca.ciclista >= 0)  { "Id do ciclista não pode ser negativo" }

        val valor = novaCobranca.valor
        val ciclistaId = novaCobranca.ciclista

        val ciclista = aluguelClient.getCiclista(ciclistaId)
            ?: throw BrokenRequirementException("Erro ao obter ciclista: $ciclistaId")

        val cartaoDeCredito = aluguelClient.getCartaoDeCredito(ciclistaId)
            ?: throw BrokenRequirementException("Erro ao obter o cartão de crédito do ciclista: $ciclistaId")

        val cobrancaReposta = cartaoDeCreditoService.enviarCobranca(valor, cartaoDeCredito, ciclista)

        if(cobrancaReposta.status != StatusPagamentoEnum.PAGA) {
            throw BrokenRequirementException("Não foi possível enviar a cobranca. ${cobrancaReposta.erros}")
        }

        val horaFinalizacao = LocalDateTime.now()
        val cobranca = Cobranca(ciclista = ciclistaId, valor = valor,
            status = cobrancaReposta.status, horaSolicitacao = horaSolicitacao, horaFinalizacao = horaFinalizacao)

        return cobrancaRepository.save(cobranca)
    }

    fun obterCobranca(idCobranca: Long): Cobranca {
        return cobrancaRepository.findById(idCobranca).orElseThrow {
            ResourceNotFoundException("Cobrança não encontrada, id: $idCobranca") }
    }

    fun colocarNaFilaDeCobranca(novaCobranca: Cobranca): Cobranca {
        val horaSolicitacao = LocalDateTime.now()
        val horaFinalizacao = LocalDateTime.now()

        val cobranca =  Cobranca(
            ciclista = novaCobranca.ciclista, valor = novaCobranca.valor, status = StatusPagamentoEnum.PENDENTE,
            horaSolicitacao = horaSolicitacao, horaFinalizacao = horaFinalizacao)

        return cobrancaRepository.save(cobranca)
    }

    fun processarCobrancasEmFila() : List<Cobranca> {
        val cobrancasEmfila = cobrancaRepository.findByStatus(StatusPagamentoEnum.PENDENTE)

        return cobrancasEmfila.map { cobranca -> enviarCobranca(cobranca) }
    }
}