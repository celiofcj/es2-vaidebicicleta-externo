package com.es2.vadebicicleta.externo.email.repository;

import com.es2.vadebicicleta.externo.email.model.RequisicaoEmail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<RequisicaoEmail, Long> {
}
