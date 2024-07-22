package com.es2.vadebicicleta.externo.dominio

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class Cobranca (
    val ciclista : Long,
    val valor: Long,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long? = null,
    val status : StatusPagamentoEnum? = null,
    val horaSolicitacao : LocalDateTime? = null,
    val horaFinalizacao : LocalDateTime? = null,
    @JsonIgnore
    val filaDeCobranca : Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Cobranca

        if (ciclista != other.ciclista) return false
        if (valor != other.valor) return false
        if (id != other.id) return false
        if (status != other.status) return false
        if (horaSolicitacao != other.horaSolicitacao) return false
        if (horaFinalizacao != other.horaFinalizacao) return false
        if (filaDeCobranca != other.filaDeCobranca) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ciclista.hashCode()
        result = 31 * result + (valor.hashCode())
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + (status?.hashCode() ?: 0)
        result = 31 * result + (horaSolicitacao?.hashCode() ?: 0)
        result = 31 * result + (horaFinalizacao?.hashCode() ?: 0)
        result = 31 * result + filaDeCobranca.hashCode()
        return result
    }

    override fun toString(): String {
        return "Cobranca(ciclista=$ciclista, valor=$valor, id=$id, status=$status, horaSolicitacao=$horaSolicitacao, horaFinalizacao=$horaFinalizacao, filaDeCobranca=$filaDeCobranca)"
    }
}