package de.doit.searchapi

import com.fasterxml.jackson.databind.JsonNode
import com.ninjasquad.springmockk.MockkBean
import de.doit.searchapi.domain.model.Job
import de.doit.searchapi.domain.model.JobId
import de.doit.searchapi.domain.service.SearchService
import io.mockk.coEvery
import org.assertj.core.api.Assertions.assertThat
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = RANDOM_PORT)
class GraphqlSearchApiTest(@Autowired private val testClient: WebTestClient) {

    private val easyRandom = EasyRandom()

    @MockkBean private lateinit var searchService: SearchService

    @Test
    internal fun testJobByIdQuery(@Value("classpath:graphql/job-by-id-query.graphql") jobByIdQuery: Resource) {
        val jobById = easyRandom.nextObject(Job::class.java)
        //TODO why is it not working with any()
        coEvery { searchService.byId(JobId("bf9c0a53-5001-4bbb-bccf-a1ad6f12ad61")) } returns jobById

        testClient.post()
                .uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(toPayload(jobByIdQuery))
                .exchange()
                .expectStatus().isOk
                .expectBody<JsonNode>()
                .consumeWith {
                    assertThat(it.responseBody).isNotNull
                    with(it.responseBody!!.get("data").get("job")) {
                        assertThat(get("id").asText()).isEqualTo(jobById.id.value)
                        assertThat(get("vendorId").asText()).isEqualTo(jobById.vendorId.value)
                        assertThat(get("title").asText()).isEqualTo(jobById.title)
                        assertThat(get("description").asText()).isEqualTo(jobById.description)
                    }
                }
    }

    companion object {

        fun toPayload(queryResource: Resource): Map<String, String?> {
            val queryString = queryResource.file.readText()

            return mapOf(
                    "query" to queryString,
                    "variables" to null
            )
        }

    }

}