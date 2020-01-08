package de.doit.searchapi

import com.fasterxml.jackson.databind.JsonNode
import com.ninjasquad.springmockk.MockkBean
import de.doit.searchapi.domain.model.Job
import de.doit.searchapi.domain.model.JobId
import de.doit.searchapi.domain.model.Query
import de.doit.searchapi.domain.model.Query.Location
import de.doit.searchapi.domain.service.SearchService
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.flow.flowOf
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
        coEvery { searchService.byId(JobId(any())) } returns jobById

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
                        assertThat(get("latitude").asDouble()).isEqualTo(jobById.latitude)
                        assertThat(get("longitude").asDouble()).isEqualTo(jobById.longitude)
                        assertThat(get("payment").asText()).isEqualTo(jobById.payment.toPlainString())
                        assertThat(get("createdAt").asText()).isEqualTo(jobById.createdAt.toString())
                        assertThat(get("modifiedAt")?.asText()).isEqualTo(jobById.modifiedAt?.toString())
                    }
                }

        coVerify { searchService.byId(JobId("bf9c0a53-5001-4bbb-bccf-a1ad6f12ad61")) }
    }

    @Test
    internal fun testJobByLocationQuery(@Value("classpath:graphql/jobs-by-location.graphql") locationQuery: Resource) {
        val jobForLocation = easyRandom.nextObject(Job::class.java)
        coEvery { searchService.search(any()) } returns flowOf(jobForLocation)

        testClient.post()
                .uri("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(toPayload(locationQuery))
                .exchange()
                .expectStatus().isOk
                .expectBody<JsonNode>()
                .consumeWith {
                    assertThat(it.responseBody).isNotNull
                    assertThat(it.responseBody!!.get("data").withArray<JsonNode>("jobs"))
                            .hasSize(1)
                            .first()
                            .satisfies { jobNode ->
                                assertThat(jobNode.get("id").asText()).isEqualTo(jobForLocation.id.value)
                                assertThat(jobNode.get("vendorId").asText()).isEqualTo(jobForLocation.vendorId.value)
                                assertThat(jobNode.get("title").asText()).isEqualTo(jobForLocation.title)
                                assertThat(jobNode.get("description").asText()).isEqualTo(jobForLocation.description)
                                assertThat(jobNode.get("latitude").asDouble()).isEqualTo(jobForLocation.latitude)
                                assertThat(jobNode.get("longitude").asDouble()).isEqualTo(jobForLocation.longitude)
                                assertThat(jobNode.get("payment").asText()).isEqualTo(jobForLocation.payment.toPlainString())
                                assertThat(jobNode.get("createdAt").asText()).isEqualTo(jobForLocation.createdAt.toString())
                                assertThat(jobNode.get("modifiedAt")?.asText()).isEqualTo(jobForLocation.modifiedAt?.toString())
                            }
                }

        coVerify { searchService.search(Query(location = Location(51.0, -2.0), distance = 50.0)) }
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