package de.doit.searchapi.domain.service

import de.doit.searchapi.domain.model.Job
import de.doit.searchapi.domain.model.JobId
import de.doit.searchapi.domain.model.Query
import de.doit.searchapi.domain.model.VendorId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class SearchService {

    fun search(query: Query): Flow<Job> {
        return emptyFlow()
    }

    suspend fun byId(jobId: JobId): Job? {
        return withContext(Dispatchers.Default) {
            Job(
                    jobId,
                    VendorId("kjdshfks"),
                    "dfs",
                    "dsfs",
                    3453.4564,
                    54534.3453,
                    BigDecimal.valueOf(9.99),
                    345345345,
                    123123123
            )
        }
    }

}