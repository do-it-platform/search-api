package de.doit.searchapi

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.Resource
import org.springframework.http.MediaType.TEXT_HTML
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@SpringBootApplication
class SearchApiApplication {

	@Bean
	fun router(@Value("classpath:graphiql.html") graphiqlHtml: Resource) = router {
		GET("/") { ServerResponse.ok().contentType(TEXT_HTML).bodyValue(graphiqlHtml) }
	}

}

fun main(args: Array<String>) {
	runApplication<SearchApiApplication>(*args)
}
