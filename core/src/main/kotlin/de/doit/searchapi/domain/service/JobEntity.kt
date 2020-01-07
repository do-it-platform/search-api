package de.doit.searchapi.domain.service

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.core.geo.GeoPoint
import java.math.BigDecimal

@Document(indexName = "jobs", type = "_doc")
data class JobEntity(@Id val id: String,
                     val vendorId: String,
                     val title: String,
                     val description: String,
                     val location: GeoPoint,
                     val payment: BigDecimal,
                     val createdAt: Long,
                     val modifiedAt: Long?)