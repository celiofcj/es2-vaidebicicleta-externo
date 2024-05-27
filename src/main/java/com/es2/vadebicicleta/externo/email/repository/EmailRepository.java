package com.es2.vadebicicleta.externo.email.repository;

import com.es2.vadebicicleta.externo.commons.repository.NoPersistenceRepository;
import com.es2.vadebicicleta.externo.email.model.RequisicaoEmail;
import org.springframework.stereotype.Repository;

@Repository
public class EmailRepository extends NoPersistenceRepository<RequisicaoEmail> {

}
