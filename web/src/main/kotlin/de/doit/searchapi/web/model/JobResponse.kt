package de.doit.searchapi.web.model

data class JobResponse(val id: String,
                       val vendorId: String,
                       val title: String,
                       val description: String,
                       val latitude: Double,
                       val longitude: Double,
                       val payment: String,
                       val createdAt: String,
                       val modifiedAt: String?)