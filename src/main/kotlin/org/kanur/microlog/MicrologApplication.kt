package org.kanur.microlog

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableAutoConfiguration
class MicrologApplication

fun main(args: Array<String>) {
	runApplication<MicrologApplication>(*args)
}
