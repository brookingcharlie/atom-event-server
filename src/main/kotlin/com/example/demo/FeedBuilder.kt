package com.example.demo

import com.rometools.rome.feed.atom.Content
import com.rometools.rome.feed.atom.Entry
import com.rometools.rome.feed.atom.Feed
import com.rometools.rome.feed.atom.Link
import com.rometools.rome.feed.atom.Person
import java.time.OffsetDateTime
import java.util.Base64
import java.util.Date
import java.util.UUID

class FeedBuilder() {
    fun build(selfHref: String, feedTitle: String, feedAuthorName: String, events: List<Event>): Feed {
        return Feed().apply {
            feedType = "atom_1.0"
            id = "urn:uuid:${UUID.randomUUID()}"
            title = feedTitle
            otherLinks = listOf(Link().apply { rel = "self"; href = "${selfHref}" })
            authors = listOf(Person().apply { name = feedAuthorName })
            updated = Date()
            entries = events.map { buildEntry(it) }
        }
    }

    private fun buildEntry(event: Event): Entry {
        return Entry().apply {
            title = "Event ${event.id}"
            id = "urn:uuid:${event.id}"
            updated = date(event.timestamp)
            contents = listOf(Content().apply {
                type = "application/json"
                value = base64(event.payload)
            })
            summary = Content().apply { type = "text/plain"; value = "Event ID ${event.id}" }
        }
    }

    private fun date(offsetDateTime: OffsetDateTime) = Date.from(offsetDateTime.toInstant());
    private fun base64(s: String) = Base64.getEncoder().encodeToString(s.toByteArray(Charsets.UTF_8))
}