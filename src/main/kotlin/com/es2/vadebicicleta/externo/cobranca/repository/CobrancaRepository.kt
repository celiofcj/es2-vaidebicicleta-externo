package com.es2.vadebicicleta.externo.cobranca.repository

import com.es2.vadebicicleta.externo.cobranca.model.Cobranca
import org.springframework.data.jpa.repository.JpaRepository

interface CobrancaRepository : JpaRepository<Cobranca, Long> {
    fun findByFilaDeCobrancaTrue() : List<Cobranca>
}


