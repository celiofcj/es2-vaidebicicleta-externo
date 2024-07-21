package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet

import com.es2.vadebicicleta.externo.cartaocredito.model.CartaoDeCredito
import net.authorize.api.contract.v1.ArrayOfLineItem
import net.authorize.api.contract.v1.CreditCardType
import net.authorize.api.contract.v1.LineItemType
import net.authorize.api.contract.v1.PaymentType
import java.math.BigDecimal
import java.time.Instant

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

fun validationTransactionItem(numeroCartaoDeCredito: String) : ArrayOfLineItem {
    val item = LineItemType()
    item.itemId = "vl$numeroCartaoDeCredito${Instant.now().toEpochMilli()}"
    item.name = "Validação de cartão de crédito "
    item.quantity = BigDecimal.ONE
    item.unitPrice = BigDecimal.valueOf(0.01)


    val arrayOfLineItem = object : ArrayOfLineItem() {
        init {
            lineItem = mutableListOf()
        }

        fun addLineItem(item : LineItemType) = lineItem.add(item)
    }

    arrayOfLineItem.addLineItem(item)
    return arrayOfLineItem
}