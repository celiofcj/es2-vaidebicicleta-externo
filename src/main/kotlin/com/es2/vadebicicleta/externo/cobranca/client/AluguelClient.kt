package com.es2.vadebicicleta.externo.cobranca.client

import com.es2.vadebicicleta.externo.dominio.CartaoDeCredito
import com.es2.vadebicicleta.externo.dominio.Ciclista
import org.springframework.stereotype.Component
import java.time.LocalDate

interface AluguelClient {
    fun getCartaoDeCredito(idCiclista: Long) : CartaoDeCredito?
    fun getCiclista(idCiclista: Long) : Ciclista?
}

@Component
class AluguelClientDefaultImp : AluguelClient {
    override fun getCartaoDeCredito(idCiclista: Long): CartaoDeCredito? {
        return if(idCiclista == 8L) CartaoDeCredito(
            "Celio Celio Celio", "5424000000000015",
            LocalDate.of(2024, 11, 11), "901")
        else CartaoDeCredito("Celio", "3088000000000017",
            LocalDate.of(2025, 1, 31), "900")
    }

    override fun getCiclista(idCiclista: Long): Ciclista? {
        return if(idCiclista == 8L) Ciclista(8, "ATIVO", "Celio Celio Celio",
            LocalDate.of(2003, 6, 9), "BRASILEIRO", "celiofcjunior@hotmail.com",
            "", "11111111111")
        else Ciclista(15, "ATIVO", "Celio", LocalDate.of(2000, 1, 9),
            "BRASILEIRO", "celio@gmail.com", "", "59616398008")
    }
}