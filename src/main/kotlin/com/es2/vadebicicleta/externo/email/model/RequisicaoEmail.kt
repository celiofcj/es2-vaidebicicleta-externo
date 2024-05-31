package com.es2.vadebicicleta.externo.email.model

import jakarta.persistence.*


@Entity
class RequisicaoEmail (
    @Column
    var email: String? = null,

    @Column
    var assunto: String? = null,

    @Column
    var mensagem: String? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
) {
    constructor(original: RequisicaoEmail) : this(original.email, original.assunto, original.mensagem, original.id)
}
