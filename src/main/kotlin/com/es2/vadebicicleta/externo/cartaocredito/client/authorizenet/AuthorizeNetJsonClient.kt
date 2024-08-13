package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet

import com.es2.vadebicicleta.externo.cartaocredito.client.OperadoraCartaoDeCreditoClient
import com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.request.*
import com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.response.CreateTransactionResponse
import com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.response.ErrorDetail
import com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet.dominio.response.TransactionResponse
import com.es2.vadebicicleta.externo.commons.exception.ExternalServiceException
import com.es2.vadebicicleta.externo.dominio.CartaoDeCredito
import com.es2.vadebicicleta.externo.dominio.CartaoDeCreditoCobrancaStatus
import com.es2.vadebicicleta.externo.dominio.CartaoDeCreditoValidacaoStatus
import com.es2.vadebicicleta.externo.dominio.Ciclista
import io.github.oshai.kotlinlogging.KotlinLogging
import net.authorize.api.MessageTypeEnum
import org.springframework.http.HttpStatus
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
            Payment(creditCard),
            poNumber = poNumber,
        )

        val authentication = merchantAuthentication()

        val transactionRequest = CreateTransactionRequest(
            authentication,
            transactionObject
        )

        val responseBody = request(transactionRequest)

        val errosDeValidacao = verificaErrosDeValidacao(responseBody.transactionResponse
            ?: throw ExternalServiceException("Erro na integracao com Authorize.Net."))

        if(errosDeValidacao.isNotEmpty()) return CartaoDeCreditoValidacaoStatus(false, errosDeValidacao)

        anularTransacao(responseBody.transactionResponse.transId ?:
            throw ExternalServiceException("Erro na integracao com Authorize.Net."))

        return CartaoDeCreditoValidacaoStatus(true)
    }

    override fun enviarCobranca(
        valor: BigDecimal,
        cartaoDeCredito: CartaoDeCredito,
        ciclista: Ciclista,
    ): CartaoDeCreditoCobrancaStatus {
        TODO("Not yet implemented")
    }

    private fun anularTransacao(transId: String) {
        val transactionObject = TransactionRequest(
            TransactionType.VOID_TRANSACTION,
            refTransId = transId
        )

        val merchantAuthentication = merchantAuthentication()

        val transactionRequest = CreateTransactionRequest(
            merchantAuthentication,
            transactionObject,
        )

        request(transactionRequest)
    }

    private fun merchantAuthentication(): MerchantAuthentication {
        return MerchantAuthentication(
            authorizeNetConfig.id,
            authorizeNetConfig.key
        )
    }

    private fun request(transactionRequest: CreateTransactionRequest): CreateTransactionResponse {
        val requestBody = CreateTransactionRequestWrapped(transactionRequest)
        val response: ResponseEntity<CreateTransactionResponse> =
            restTemplate.postForEntity(authorizeNetConfig.url, requestBody)

        if (response.statusCode != HttpStatus.OK) throw ExternalServiceException(
            "Erro na integracao com Authorize.Net." +
                    " HTTP Status code inesperado: ${response.statusCode}"
        )

        val responseBody = response.body ?: throw ExternalServiceException("Erro na integracao com Authorize.Net.")

        return responseBody
    }

    private fun verificaErrosDeValidacao(transactionResponse: TransactionResponse) : List<String> {
        val erroList = mutableListOf<String>()
        if (transactionResponse.responseCode != "1")
            erroList.add("Cartao de credito invalido")

        if (transactionResponse.cvvResultCode != "" && transactionResponse.cvvResultCode != "M")
            erroList.add("CVV invalido. Authorize.Net")

        if (!transactionResponse.errors.isNullOrEmpty()) {
            if (transactionResponse.errors.any { it?.errorCode == "8" })
                erroList.add("Cartao de credito venciado")
            else erroList.add("Operacao mal-sucedida. Consulte a operadora para obter mais detalhes")
        }

        return erroList.toList()
    }
}