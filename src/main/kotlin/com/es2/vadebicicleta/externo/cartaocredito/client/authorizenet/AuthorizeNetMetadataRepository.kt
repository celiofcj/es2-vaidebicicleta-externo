package com.es2.vadebicicleta.externo.cartaocredito.client.authorizenet

import org.springframework.data.jpa.repository.JpaRepository


interface AuthorizeNetMetadataRepository : JpaRepository<AuthorizeNetMetadata, Long> {
    fun findByCardNumber(creditCardNumber: String): AuthorizeNetMetadata?
}