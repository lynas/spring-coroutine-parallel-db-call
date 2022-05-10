package com.reev.reactivespring

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactive.asFlow
import org.reactivestreams.Publisher
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.Instant
import java.util.*


@SpringBootApplication
class ReactiveSpringDemoApplication

fun main(args: Array<String>) {
    runApplication<ReactiveSpringDemoApplication>(*args)
}


@Table
data class Consumer(
    @Id
    val id: UUID,
    val type: String,
    @Column("created_at")
    val createdAt: Instant
)

interface ConsumerRepository: ReactiveCrudRepository<Consumer, UUID>{

    fun findByType(type: String) : Flux<Consumer>
}

@Service
class ConsumerService(val consumerRepository: ConsumerRepository){

    suspend fun findByType(type: String) : Publisher<Consumer> {
        delay(2000)
        return consumerRepository.findByType(type)
    }

    suspend fun findByTypeRegular(type: String) : ConsumerDTO {
        val p1 = findByType(type)
        val p2 = findByType(type)
        val p3 = findByType(type)
        return ConsumerDTO(p1,p2,p3)
    }

    suspend fun findByTypeRegulaParallel(type: String): ConsumerDTO {
        val p1 = CoroutineScope(IO).async { findByType(type) }
        val p2 = CoroutineScope(IO).async { findByType(type) }
        val p3 = CoroutineScope(IO).async { findByType(type) }
        return ConsumerDTO(p1.await(), p2.await(), p3.await())
    }

}

data class ConsumerDTO(
    val p1 : Publisher<Consumer>,
    val p2 : Publisher<Consumer>,
    val p3 : Publisher<Consumer>,
)

@RestController
@RequestMapping("/consumer")
class ConsumerController(val consumerService: ConsumerService){

    @GetMapping("/{type}")
    suspend fun getByType(@PathVariable type: String) =
        consumerService.findByTypeRegular(type).p1.asFlow()

    @GetMapping("/p/{type}")
    suspend fun getByTypeParallel(@PathVariable type: String)  =
        consumerService.findByTypeRegulaParallel(type).p1.asFlow()
}









