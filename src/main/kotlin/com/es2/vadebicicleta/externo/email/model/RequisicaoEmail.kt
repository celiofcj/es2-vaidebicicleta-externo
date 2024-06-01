package com.es2.vadebicicleta.externo.email.model

import jakarta.persistence.*

@Entity
class RequisicaoEmail (
    @Column(nullable = false)
    var email: String,

    @Column(nullable = false)
    var assunto: String,

    @Column(nullable = false)
    var mensagem: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
)
