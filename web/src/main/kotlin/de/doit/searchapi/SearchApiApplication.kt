package de.doit.searchapi

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.TEXT_HTML
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import org.springframework.web.server.WebFilter

@SpringBootApplication
class SearchApiApplication {

	@Bean
	fun router(@Value("classpath:graphiql.html") graphiqlHtml: Resource) = router {
		GET("/") { ServerResponse.ok().contentType(TEXT_HTML).bodyValue(graphiqlHtml) }
	}

	// needed until this https://github.com/graphql-java-kickstart/graphql-spring-boot/issues/328 is fixed
	@Bean
	fun forceJsonContentTypeWithoutCharsetForGraphqlEndpoint(): WebFilter {
		return WebFilter { serverWebExchange, webFilterChain ->
			serverWebExchange
					.takeIf { it.request.path.value() == "/graphql" }
					?.run {
						webFilterChain.filter(this
								.mutate()
								.request { it.header(CONTENT_TYPE, APPLICATION_JSON_VALUE).build() }
								.build()
						)
					} ?: webFilterChain.filter(serverWebExchange)
		}
	}

}

fun main(args: Array<String>) {
	runApplication<SearchApiApplication>(*args)
}
