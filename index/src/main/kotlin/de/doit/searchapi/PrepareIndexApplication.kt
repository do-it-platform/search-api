package de.doit.searchapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PrepareIndexApplication

fun main(args: Array<String>) {
    runApplication<PrepareIndexApplication>(*args)
}