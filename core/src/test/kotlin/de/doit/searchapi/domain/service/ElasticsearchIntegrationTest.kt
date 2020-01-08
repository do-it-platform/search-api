package de.doit.searchapi.domain.service

import de.doit.searchapi.domain.model.Job
import de.doit.searchapi.domain.model.JobId
import de.doit.searchapi.domain.model.Query
import de.doit.searchapi.domain.model.Query.Location
import de.doit.searchapi.domain.model.VendorId
import de.doit.searchapi.domain.service.ElasticsearchIntegrationTest.ElasticsearchContextInitializer
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.ObjectAssert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.BindMode.READ_ONLY
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal

@Testcontainers
@SpringBootTest
@ContextConfiguration(initializers = [ElasticsearchContextInitializer::class])
internal class ElasticsearchIntegrationTest(@Autowired private val searchService: SearchService) {

    companion object {
        private const val ES_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch-oss:7.5.1"
        @Container private val esContainer: ElasticsearchContainer = ElasticsearchContainer(ES_IMAGE)
                .withClasspathResourceMapping("/job-test-data.json", "/job-test-data.json", READ_ONLY)
                .withFileSystemBind("../index/mapping.json", "/mapping.json", READ_ONLY)
    }

    @Test
    internal fun testEsMappingOfSampleData() = runBlocking {
        val jobId = JobId("bf9c0a53-5001-4bbb-bccf-a1ad6f12ad61")
        val jobById = searchService.byId(jobId)

        assertThat(jobById).isNotNull
        assertThat(jobById!!).containsMappedTestData()
    }

    @Test
    internal fun testLocationSearch() = runBlocking {
        val geoSearchResult = searchService.search(Query(location = Location(51.0, -2.0))).toList()

        assertThat(geoSearchResult)
                .hasSize(1)
                .first().containsMappedTestData()
    }

    @Test
    internal fun searchWithEmptyQueryShouldReturnAll() = runBlocking {
        val geoSearchResult = searchService.search(Query()).toList()

        assertThat(geoSearchResult)
                .hasSize(1)
                .first().containsMappedTestData()
    }


    private fun ObjectAssert<Job>.containsMappedTestData() {
        satisfies {
            it.apply {
                assertThat(this.id).isEqualTo(JobId("bf9c0a53-5001-4bbb-bccf-a1ad6f12ad61"))
                assertThat(this.vendorId).isEqualTo(VendorId("12345"))
                assertThat(this.title).isEqualTo("Tutoring")
                assertThat(this.description).isEqualTo("Need help with math")
                assertThat(this.latitude).isEqualTo(51.668001)
                assertThat(this.longitude).isEqualTo(-1.0884338)
                assertThat(this.payment).isEqualTo(BigDecimal.valueOf(7.99))
                assertThat(this.createdAt).isEqualTo(1578335992360)
                assertThat(this.modifiedAt).isNull()
            }
        }
    }

    class ElasticsearchContextInitializer: ApplicationContextInitializer<ConfigurableApplicationContext> {

        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            val esAddress = "${esContainer.containerIpAddress}:${esContainer.getMappedPort(9200)}"

            esContainer.execInContainer(
                    "curl", "-XPUT", "http://localhost:9200/jobs",
                    "-H", "Content-Type: application/json",
                    "--data", "@/mapping.json"
            )

            esContainer.execInContainer(
                    "curl", "-XPOST", "http://localhost:9200/_bulk",
                    "-H", "Content-Type: application/json",
                    "--data-binary", "@/job-test-data.json"
            )

            TestPropertyValues
                    .of("spring.data.elasticsearch.client.reactive.endpoints=${esAddress}")
                    .applyTo(applicationContext)
        }

    }

    @SpringBootApplication
    class TestApp

}