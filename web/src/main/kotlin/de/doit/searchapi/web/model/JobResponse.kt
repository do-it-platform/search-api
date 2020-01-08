package de.doit.searchapi.web.model

import de.doit.searchapi.domain.model.JobId
import de.doit.searchapi.domain.model.VendorId

data class JobResponse(val id: JobId,
                       val vendorId: VendorId,
                       val title: String,
                       val description: String,
                       val latitude: Double,
                       val longitude: Double,
                       val payment: String,
                       val createdAt: String,
                       val modifiedAt: String?)