package com.es2.vadebicicleta.externo.commons.repository

import com.es2.vadebicicleta.externo.commons.repository.manager.LongIdManager
import com.es2.vadebicicleta.externo.commons.repository.manager.LongIdManager.LongGenerationStrategy
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import java.util.*
import java.util.function.Consumer

@Repository
abstract class NoPersistenceRepository<T : Identificable<Long?>?> @Autowired protected constructor(private val longIdManager: LongIdManager) {
    private val elements: MutableMap<Long, T> = mutableMapOf()

    fun save(o: T?): T? {
        if(o == null){
            return null
        }

        if (o.id == null) {
            val id : Long = longIdManager.generateId(javaClass, LongGenerationStrategy.IDENTITY)
            o.id = id
        } else {
            val saved = findById(o.id!!)
            if (saved.isEmpty) {
                throw ElementNotFoundException(
                    "Not possible to update element with id: " +
                            o.id + ". Does not exists element with this id"
                )
            }
        }
        elements[o.id!!] = o
        return o
    }

    fun saveAll(entities: Iterable<T>): Iterable<T> {
        val it = entities.iterator()
        val returnList: MutableList<T> = ArrayList()
        while (it.hasNext()) {
            val saved = save(it.next()) ?: continue
            returnList.add(saved)
        }
        return returnList
    }

    fun findById(id: Long): Optional<T & Any> {
        val element = elements[id] ?: return Optional.empty()
        return Optional.of(element)
    }

    fun existsById(id: Long): Boolean {
        return elements.containsKey(id)
    }

    fun findAll(): Iterable<T> {
        return ArrayList(elements.values)
    }

    fun findAllById(ids: Iterable<Long>): Iterable<T> {
        val it = ids.iterator()
        val returnList: MutableList<T> = ArrayList()
        while (it.hasNext()) {
            val next = it.next()
            val element = elements[next]
            if (element != null) {
                returnList.add(element)
            }
        }
        return returnList
    }

    fun count(): Long {
        return elements.size.toLong()
    }

    fun deleteById(id: Long) {
        elements.remove(id)
    }

    fun delete(o: T) {
        elements.remove(o?.id)
    }

    fun deleteAllById(ids: Iterable<Long>) {
        ids.forEach(Consumer { id: Long -> deleteById(id) })
    }

    fun deleteAll(entities: Iterable<T>) {
        entities.forEach(Consumer { o: T -> delete(o) })
    }

    fun deleteAll() {
        elements.clear()
    }
}
