package de.doit.searchapi.domain.model

import java.math.BigDecimal

data class Job(val id: JobId,
               val vendorId: VendorId,
               val title: String,
               val description: String,
               val location: Location,
               val payment: BigDecimal,
               val createdAt: Long,
               val modifiedAt: Long?) {

    data class Location(val lat: Double, val lon: Double)

}