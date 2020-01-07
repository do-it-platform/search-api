package de.doit.searchapi.domain.model

import java.math.BigDecimal

data class Job(val id: JobId,
               val vendorId: VendorId,
               val title: String,
               val description: String,
               val latitude: Double,
               val longitude: Double,
               val payment: BigDecimal,
               val createdAt: Long,
               val modifiedAt: Long?)