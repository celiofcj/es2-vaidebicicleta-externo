package com.es2.vadebicicleta.externo.cobranca.service

import com.es2.vadebicicleta.externo.cartaocredito.client.OperadoraClient
import com.es2.vadebicicleta.externo.cobranca.client.AluguelClient
import com.es2.vadebicicleta.externo.cobranca.model.Cobranca
import com.es2.vadebicicleta.externo.cobranca.model.NovaCobranca
import com.es2.vadebicicleta.externo.cobranca.repository.CobrancaRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CobrancaService(
    val aluguelClient : AluguelClient,
    val operadoraClient: OperadoraClient,
    val cobrancaRepository: CobrancaRepository
) {

    fun enviarCobranca(novaCobranca: NovaCobranca) : Cobranca {
        val horaSolicitacao = LocalDateTime.now();
        val ciclista = novaCobranca.ciclista
        val valor = if(novaCobranca.valor >= 0) {
            novaCobranca.valor
        } else {
            throw IllegalArgumentException("Valor não pode ser negativo")
        }

        val cartaoDeCredito = aluguelClient.getCartaoDeCredito(ciclista)
            ?: throw BrokenRequirementException("Erro ao obter o cartão de crédito do ciclista: $ciclista")

        val statusPagamento = operadoraClient.enviarCobranca(cartaoDeCredito)

        val horaFinalizacao = LocalDateTime.now()
        val cobranca = Cobranca(ciclista = ciclista, valor = valor,
            status = statusPagamento, horaSolicitacao = horaSolicitacao, horaFinalizacao = horaFinalizacao)

        return cobrancaRepository.save(cobranca)
    }
}