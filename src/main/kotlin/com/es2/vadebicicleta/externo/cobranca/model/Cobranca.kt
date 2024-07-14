package com.es2.vadebicicleta.externo.cobranca.model

import com.es2.vadebicicleta.externo.cartaocredito.model.StatusPagamentoEnum
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class Cobranca (
    val ciclista : Long? = null,
    val valor: Long? = null,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null,
    val status : StatusPagamentoEnum? = null,
    val horaSolicitacao : LocalDateTime? = null,
    val horaFinalizacao : LocalDateTime? = null
)