package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet

import com.es2.vadebicicleta.externo.dominio.CartaoDeCredito
import com.es2.vadebicicleta.externo.dominio.Ciclista
import net.authorize.api.CreditCardType
import net.authorize.api.CustomerDataType
import net.authorize.api.CustomerTypeEnum
import net.authorize.api.PaymentType

fun getCreditCardPaymentType(cartaoDeCredito : CartaoDeCredito) : PaymentType {
    val numero = cartaoDeCredito.numero
    val dataDeVencimento = "${cartaoDeCredito.validade.month.value}${cartaoDeCredito.validade.year}"
    val cvv = cartaoDeCredito.cvv

    val creditCard = CreditCardType().apply {
        cardNumber = numero
        expirationDate = dataDeVencimento
        cardCode = cvv
    }

    val paymentType = PaymentType().apply {
        this.creditCard = creditCard
    }

    return paymentType
}

fun getIndividualCustomer(ciclista: Ciclista) : CustomerDataType {
    val customer = CustomerDataType().apply {
        type = CustomerTypeEnum.INDIVIDUAL
        id = ciclista.id.toString()
        email = ciclista.email
    }

    return customer
}