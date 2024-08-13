package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet

import com.es2.vadebicicleta.externo.cartaocredito.client.OperadoraCartaoDeCreditoClient
import com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.request.*
import com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.response.CreateTransactionResponse
import com.es2.vadebicicleta.externo.dominio.CartaoDeCredito
import com.es2.vadebicicleta.externo.dominio.CartaoDeCreditoCobrancaStatus
import com.es2.vadebicicleta.externo.dominio.CartaoDeCreditoValidacaoStatus
import com.es2.vadebicicleta.externo.dominio.Ciclista
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity
import java.math.BigDecimal
import java.time.Instant

private val logger = KotlinLogging.logger {}

@Service
class AuthorizeNetJsonClient(private val authorizeNetConfig: AuthorizeNetConfig, private val restTemplate: RestTemplate)
    : OperadoraCartaoDeCreditoClient {

    override fun validarCartaoDeCredito(cartaoDeCredito: CartaoDeCredito): CartaoDeCreditoValidacaoStatus {
        val creditCard = CreditCard(cartaoDeCredito)
        val poNumber = "vl${Instant.now().nano}"

        val transactionObject = TransactionRequest(
            TransactionType.AUTH_ONLY_TRANSACTION,
            BigDecimal.valueOf(0.01),
            Payment( creditCard),
            poNumber = poNumber,
        )

        val authentication = MerchantAuthentication(
            authorizeNetConfig.id,
            authorizeNetConfig.key
        )

        val transactionRequest = CreateTransactionRequest(
            authentication,
            transactionObject
        )

        val body = CreateTransactionRequestWrapped(transactionRequest)

        val response : ResponseEntity<CreateTransactionResponse> =
            restTemplate.postForEntity(authorizeNetConfig.url, body)

        return CartaoDeCreditoValidacaoStatus(true)
    }

    override fun enviarCobranca(
        valor: BigDecimal,
        cartaoDeCredito: CartaoDeCredito,
        ciclista: Ciclista,
    ): CartaoDeCreditoCobrancaStatus {
        TODO("Not yet implemented")
    }


}