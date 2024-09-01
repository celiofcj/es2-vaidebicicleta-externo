package com.es2.vadebicicleta.externo.dominio

data class Ciclista (
    val id : Long,
    val cpf : String?,
    val passaporte : Passaporte?,
    val email: String
) {
    data class Passaporte(
        val numero : String,
    )
}