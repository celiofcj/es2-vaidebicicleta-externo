package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class AuthorizeNetMetadata(
    var cardNumber: String,
    var networkTransId: String,
    @Id @GeneratedValue val id: Long? = null,
    )