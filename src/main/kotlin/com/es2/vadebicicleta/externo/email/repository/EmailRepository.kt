package com.es2.vadebicicleta.externo.email.repository

import com.es2.vadebicicleta.externo.dominio.RequisicaoEmail
import org.springframework.data.jpa.repository.JpaRepository

interface EmailRepository : JpaRepository<RequisicaoEmail?, Long?>
