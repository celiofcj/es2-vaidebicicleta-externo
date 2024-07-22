package com.es2.vadebicicleta.externo.dominio

import java.time.LocalDate

data class Ciclista (
    val id : Long,
    val status : String,
    val nome : String,
    val nascimento : LocalDate,
    val nacionalidade : String?,
    val email : String,
    val urlFotoDocumento : String,
    val cpf : String? = null,
    val passaporte : Passaporte? = null,
) {
    data class Passaporte(
        val numero : String,
        val validade : String,
        val pais : String
    )
}