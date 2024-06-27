package com.es2.vadebicicleta.externo.cobranca.model

import com.es2.vadebicicleta.externo.cartaocredito.model.StatusPagamentoEnum
import java.time.LocalDateTime

class Cobranca (
    val ciclista : Long? = null,
    val valor: Long? = null,
    val id : Long? = null,
    val status : StatusPagamentoEnum? = null,
    val horaSolicitacao : LocalDateTime? = null,
    val horaFinalizacao : LocalDateTime? = null
)