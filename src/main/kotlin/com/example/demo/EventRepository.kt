package com.example.demo

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Optional
import java.util.UUID

@Repository
class EventRepository : PagingAndSortingRepository<Event, String> {
    private val events = (0 until 11).map {
        val eventId = UUID.randomUUID().toString()
        val timestamp = OffsetDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).plusSeconds(it.toLong())
        val payload = """{"title":"Event ${eventId}"}"""
        Event(eventId, timestamp, payload)
    }

    override fun findAll(pageable: Pageable): Page<Event> {
        return PageImpl(
                events.subList(pageable.offset.toInt(), minOf(pageable.offset.toInt() + pageable.pageSize, events.count())),
                pageable,
                events.size.toLong()
        )
    }

    override fun count(): Long {
        return events.count().toLong()
    }

    override fun findAll(sort: Sort): MutableIterable<Event> {
        throw NotImplementedError()
    }

    override fun <S : Event?> save(entity: S): S {
        throw NotImplementedError()
    }

    override fun findAll(): MutableIterable<Event> {
        throw NotImplementedError()
    }

    override fun deleteById(id: String) {
        throw NotImplementedError()
    }

    override fun deleteAll(entities: MutableIterable<Event>) {
        throw NotImplementedError()
    }

    override fun deleteAll() {
        throw NotImplementedError()
    }

    override fun <S : Event?> saveAll(entities: MutableIterable<S>): MutableIterable<S> {
        throw NotImplementedError()
    }

    override fun findAllById(ids: MutableIterable<String>): MutableIterable<Event> {
        throw NotImplementedError()
    }

    override fun existsById(id: String): Boolean {
        throw NotImplementedError()
    }

    override fun findById(id: String): Optional<Event> {
        throw NotImplementedError()
    }

    override fun delete(entity: Event) {
        throw NotImplementedError()
    }
}