package com.example.demo

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.springframework.data.domain.PageRequest
import java.time.OffsetDateTime
import java.time.ZoneOffset

class EventRepositoryTest {
    @Test
    fun firstPage() {
        val events = EventRepository().findAll(PageRequest.of(0, 3))
        assertThat(events.pageable.pageNumber).isEqualTo(0)
        assertThat(events.pageable.pageSize).isEqualTo(3)
        assertThat(events.totalElements).isEqualTo(11)
        assertThat(events.content).hasSize(3)
        assertThat(events.content[0].timestamp).isEqualTo(OffsetDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC))
        assertThat(events.content[2].timestamp).isEqualTo(OffsetDateTime.of(2018, 1, 1, 0, 0, 2, 0, ZoneOffset.UTC))
    }

    @Test
    fun lastPage() {
        val events = EventRepository().findAll(PageRequest.of(3, 3))
        assertThat(events.pageable.pageNumber).isEqualTo(3)
        assertThat(events.pageable.pageSize).isEqualTo(3)
        assertThat(events.totalElements).isEqualTo(11)
        assertThat(events.content).hasSize(2)
        assertThat(events.content[0].timestamp).isEqualTo(OffsetDateTime.of(2018, 1, 1, 0, 0, 9, 0, ZoneOffset.UTC))
        assertThat(events.content[1].timestamp).isEqualTo(OffsetDateTime.of(2018, 1, 1, 0, 0, 10, 0, ZoneOffset.UTC))
    }

    @Test
    fun count() {
        assertThat(EventRepository().count()).isEqualTo(11)
    }
}