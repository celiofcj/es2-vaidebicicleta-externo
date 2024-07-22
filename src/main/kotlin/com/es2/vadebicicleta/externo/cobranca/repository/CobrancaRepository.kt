package com.es2.vadebicicleta.externo.cobranca.repository

import com.es2.vadebicicleta.externo.dominio.Cobranca
import com.es2.vadebicicleta.externo.dominio.StatusPagamentoEnum
import org.springframework.data.jpa.repository.JpaRepository

interface CobrancaRepository : JpaRepository<Cobranca, Long> {
    fun findByStatus(statusPagamentoEnum: StatusPagamentoEnum) : List<Cobranca>
}


