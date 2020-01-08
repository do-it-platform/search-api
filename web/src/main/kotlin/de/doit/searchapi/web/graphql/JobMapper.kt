package de.doit.searchapi.web.graphql

import de.doit.searchapi.domain.model.Job
import de.doit.searchapi.web.model.JobResponse
import org.mapstruct.Mapper

@Mapper
internal abstract class JobMapper {

    fun mapToDTO(job: Job): JobResponse = JobResponse(
            id = job.id,
            vendorId = job.vendorId,
            title = job.title,
            description = job.description,
            latitude = job.latitude,
            longitude = job.longitude,
            payment = job.payment.toPlainString(),
            createdAt = job.createdAt.toString(),
            modifiedAt = job.modifiedAt?.toString()
    )

}