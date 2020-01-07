package de.doit.searchapi.domain.service

import de.doit.searchapi.domain.model.Job
import de.doit.searchapi.domain.model.JobId
import de.doit.searchapi.domain.model.VendorId
import org.mapstruct.Mapper

@Mapper
internal abstract class EntityMapper {

    fun map(jobEntity: JobEntity): Job = Job(
            id = JobId(jobEntity.id),
            vendorId = VendorId(jobEntity.vendorId),
            title = jobEntity.title,
            description = jobEntity.description,
            location = Job.Location(jobEntity.location.lat, jobEntity.location.lon),
            payment = jobEntity.payment,
            createdAt = jobEntity.createdAt,
            modifiedAt = jobEntity.modifiedAt
    )

}