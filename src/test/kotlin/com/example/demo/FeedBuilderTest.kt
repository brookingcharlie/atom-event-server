package com.example.demo

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class FeedBuilderTest {
    private val events = listOf(
            Event("c0f20f7d-4e31-41a4-bf9a-76c3942f4388", OffsetDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC), """{"title":"Foo"}"""),
            Event("a3c5a322-4b08-41df-8acd-34938fb53d67", OffsetDateTime.of(2018, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC), """{"title":"Bar"}""")
    )

    @Test
    fun head() {
        val feed = FeedBuilder().build("http://localhost/feed", "Qwerty", "Harry", PageImpl(listOf(), PageRequest.of(0, 2), 0))
        assertThat(feed.feedType).isEqualTo("atom_1.0")
        assertThat(feed.title).isEqualTo("Qwerty")
        assertThat(feed.authors[0].name).isEqualTo("Harry")
        assertThat(feed.updated.toInstant()).isEqualTo(Instant.EPOCH)
        assertThat(feed.otherLinks.find {it.rel == "current"}).isNull()
        assertThat(feed.otherLinks.find {it.rel == "self"}!!.href).isEqualTo("http://localhost/feed")
        assertThat(feed.otherLinks.find {it.rel == "next-archive"}).isNull()
        assertThat(feed.otherLinks.find {it.rel == "prev-archive"}).isNull()
    }

    @Test
    fun subscriptionDocument() {
        val feed = FeedBuilder().build("http://localhost/feed", "Qwerty", "Harry", PageImpl(events, PageRequest.of(3, 2), 7))
        assertThat(feed.updated.toInstant()).isEqualTo(events[1].timestamp.toInstant())
        assertThat(feed.otherLinks.find {it.rel == "current"}).isNull()
        assertThat(feed.otherLinks.find {it.rel == "self"}!!.href).isEqualTo("http://localhost/feed")
        assertThat(feed.otherLinks.find {it.rel == "next-archive"}).isNull()
        assertThat(feed.otherLinks.find {it.rel == "prev-archive"}!!.href).isEqualTo("http://localhost/feed/2")
    }

    @Test
    fun latestArchive() {
        val feed = FeedBuilder().build("http://localhost/feed", "Qwerty", "Harry", PageImpl(events, PageRequest.of(2, 2), 7))
        assertThat(feed.updated.toInstant()).isEqualTo(events[1].timestamp.toInstant())
        assertThat(feed.otherLinks.find {it.rel == "current"}!!.href).isEqualTo("http://localhost/feed")
        assertThat(feed.otherLinks.find {it.rel == "self"}!!.href).isEqualTo("http://localhost/feed/2")
        assertThat(feed.otherLinks.find {it.rel == "next-archive"}).isNull()
        assertThat(feed.otherLinks.find {it.rel == "prev-archive"}!!.href).isEqualTo("http://localhost/feed/1")
    }

    @Test
    fun middleArchive() {
        val feed = FeedBuilder().build("http://localhost/feed", "Qwerty", "Harry", PageImpl(events, PageRequest.of(1, 2), 7))
        assertThat(feed.updated.toInstant()).isEqualTo(events[1].timestamp.toInstant())
        assertThat(feed.otherLinks.find {it.rel == "current"}!!.href).isEqualTo("http://localhost/feed")
        assertThat(feed.otherLinks.find {it.rel == "self"}!!.href).isEqualTo("http://localhost/feed/1")
        assertThat(feed.otherLinks.find {it.rel == "next-archive"}!!.href).isEqualTo("http://localhost/feed/2")
        assertThat(feed.otherLinks.find {it.rel == "prev-archive"}!!.href).isEqualTo("http://localhost/feed/0")
    }

    @Test
    fun earliestArchive() {
        val feed = FeedBuilder().build("http://localhost/feed", "Qwerty", "Harry", PageImpl(events, PageRequest.of(0, 2), 7))
        assertThat(feed.updated.toInstant()).isEqualTo(events[1].timestamp.toInstant())
        assertThat(feed.otherLinks.find {it.rel == "current"}!!.href).isEqualTo("http://localhost/feed")
        assertThat(feed.otherLinks.find {it.rel == "self"}!!.href).isEqualTo("http://localhost/feed/0")
        assertThat(feed.otherLinks.find {it.rel == "next-archive"}!!.href).isEqualTo("http://localhost/feed/1")
        assertThat(feed.otherLinks.find {it.rel == "prev-archive"}).isNull()
    }
}