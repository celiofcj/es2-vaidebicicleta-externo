package com.es2.vadebicicleta.externo.commons.repository.manager

import org.springframework.stereotype.Component

@Component
class LongIdManager {
    private val idCounters: MutableMap<Class<*>, Long> = mutableMapOf()

    fun generateId(repository: Class<*>, strategy: LongGenerationStrategy): Long {
        return when(strategy){
            LongGenerationStrategy.IDENTITY -> identity(repository)
        }
    }

    private fun identity(repository: Class<*>): Long {
        var idCounter = idCounters[repository]
        if (idCounter == null) {
            idCounter = 0L
        } else {
            idCounter++
        }
        idCounters[repository] = idCounter
        return idCounter
    }

    enum class LongGenerationStrategy {
        IDENTITY
    }
}
