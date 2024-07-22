package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet

import com.es2.vadebicicleta.externo.dominio.CartaoDeCredito
import net.authorize.api.contract.v1.CreditCardType
import net.authorize.api.contract.v1.PaymentType

fun getCreditCardPaymentType(cartaoDeCredito : CartaoDeCredito) : PaymentType {
    val numero = cartaoDeCredito.numero
    val dataDeVencimento = "${cartaoDeCredito.validade.month.value}${cartaoDeCredito.validade.year%100}"
    val cvv = cartaoDeCredito.cvv
    val creditCard = CreditCardType()

    creditCard.cardNumber = numero
    creditCard.expirationDate = dataDeVencimento
    creditCard.cardCode = cvv

    val paymentType = PaymentType()
    paymentType.creditCard = creditCard

    return paymentType
}