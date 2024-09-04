package com.es2.vadebicicleta.externo.restore

import jakarta.transaction.Transactional
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.springframework.http.ResponseEntity
import org.springframework.jdbc.datasource.DataSourceUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement
import javax.sql.DataSource

@RestController
class RestoreController(
    private val dataSource: DataSource,
    private val resourceLoader: ResourceLoader
) {

    @GetMapping("/restaurarDados")
    @Transactional
    fun restaurarDados(): ResponseEntity<Void> {
        try {
            val connection: Connection = DataSourceUtils.getConnection(dataSource)
            val statement: Statement = connection.createStatement()

            executeScript(statement, "classpath:DDL.sql")

            executeScript(statement, "classpath:DML.sql")

            return ResponseEntity.ok().build()
        } catch (e: Exception) {
            return ResponseEntity.internalServerError().build()
        }
    }

    @Throws(IOException::class, SQLException::class)
    private fun executeScript(statement: Statement, scriptPath: String) {
        val resource: Resource = resourceLoader.getResource(scriptPath)
        BufferedReader(InputStreamReader(resource.inputStream)).use { reader ->
            val sql = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sql.append(line).append("\n")
                if (line!!.trim().endsWith(";")) {
                    statement.execute(sql.toString())
                    sql.setLength(0) // Clear the buffer
                }
            }
        }
    }
}