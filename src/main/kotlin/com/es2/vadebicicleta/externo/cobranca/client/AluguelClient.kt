package com.es2.vadebicicleta.externo.cobranca.client

import com.es2.vadebicicleta.externo.dominio.CartaoDeCredito
import org.springframework.stereotype.Component
import java.time.LocalDate

interface AluguelClient {
    fun getCartaoDeCredito(idCiclista: Long) : CartaoDeCredito?
}

@Component
class AluguelClientDefaultImp : AluguelClient {
    override fun getCartaoDeCredito(idCiclista: Long): CartaoDeCredito? {
        return CartaoDeCredito(
            "Celio Celio Celio", "1234567890123456", LocalDate.now(), "123")
    }

}