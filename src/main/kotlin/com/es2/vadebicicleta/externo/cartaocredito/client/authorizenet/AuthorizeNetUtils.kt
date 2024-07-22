package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet

import com.es2.vadebicicleta.externo.commons.exception.ExternalServiceException
import com.es2.vadebicicleta.externo.dominio.CartaoDeCredito
import com.es2.vadebicicleta.externo.dominio.Ciclista
import net.authorize.api.contract.v1.*

fun getCreditCardPaymentType(cartaoDeCredito : CartaoDeCredito) : PaymentType {
    val numero = cartaoDeCredito.numero
    val dataDeVencimento = "${cartaoDeCredito.validade.month.value}${cartaoDeCredito.validade.year}"
    val cvv = cartaoDeCredito.cvv
    val creditCard = CreditCardType()

    creditCard.cardNumber = numero
    creditCard.expirationDate = dataDeVencimento
    creditCard.cardCode = cvv

    val paymentType = PaymentType()
    paymentType.creditCard = creditCard

    return paymentType
}

fun getIndividualCustomer(ciclista: Ciclista) : CustomerDataType {
    val customer = CustomerDataType()
    customer.type = CustomerTypeEnum.INDIVIDUAL
    customer.id = ciclista.id.toString()
    customer.email = ciclista.email

    return customer
}

fun errorsResponse(response: CreateTransactionResponse): MutableList<String> {
    if (response.messages?.resultCode == null || response.messages.resultCode != MessageTypeEnum.OK) {
        throw ExternalServiceException("Erro na integracao com Authorize.Net. ${response.messages.message}")
    }

    val result =
        response.transactionResponse ?: throw ExternalServiceException("Erro na integracao com Authorize.Net.")

    val erroList = mutableListOf<String>()
    if (!result.responseCode.equals("1")) {
        erroList.add("Cartao de credito invalido")
    }
    if (!result.cvvResultCode.equals("M")) {
        erroList.add("CVV invalido. Authorize.Net")
    }
    return erroList
}
