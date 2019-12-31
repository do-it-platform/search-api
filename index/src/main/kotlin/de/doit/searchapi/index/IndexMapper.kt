package de.doit.searchapi.index

import de.doit.jobapi.domain.event.JobAggregatedEvent
import de.doit.searchapi.domain.event.JobIndexEvent
import de.doit.searchapi.domain.event.Location
import org.mapstruct.Mapper

@Mapper
internal abstract class IndexMapper {

    fun mapToIndexEvent(jobAggregatedEvent: JobAggregatedEvent) = JobIndexEvent.newBuilder()
            .setVendorId(jobAggregatedEvent.getVendorId())
            .setTitle(jobAggregatedEvent.getTitle())
            .setDescription(jobAggregatedEvent.getDescription())
            .setLocation(Location.newBuilder()
                    .setLat(jobAggregatedEvent.getLatitude())
                    .setLon(jobAggregatedEvent.getLongitude())
                    .build())
            .setPayment(jobAggregatedEvent.getPayment())
            .setCreatedAt(jobAggregatedEvent.getCreatedAt())
            .setModifiedAt(jobAggregatedEvent.getModifiedAt())
            .build()

}