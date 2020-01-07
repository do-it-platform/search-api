package de.doit.searchapi.domain.service

import de.doit.searchapi.domain.model.JobId
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.query.Query
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

inline fun <reified T> ReactiveElasticsearchTemplate.findById(jobId: JobId): Mono<T> = findById(jobId.value, T::class.java)
inline fun <reified T> ReactiveElasticsearchTemplate.find(query: Query): Flux<T> = find(query, T::class.java)