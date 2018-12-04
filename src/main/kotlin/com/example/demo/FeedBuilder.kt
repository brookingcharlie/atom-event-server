package com.example.demo

import com.rometools.rome.feed.atom.Content
import com.rometools.rome.feed.atom.Entry
import com.rometools.rome.feed.atom.Feed
import com.rometools.rome.feed.atom.Link
import com.rometools.rome.feed.atom.Person
import org.springframework.data.domain.Page
import java.time.OffsetDateTime
import java.util.Base64
import java.util.Date
import java.util.UUID

class FeedBuilder {
    fun build(baseHref: String, feedTitle: String, feedAuthorName: String, events: Page<Event>): Feed {
        return Feed().apply {
            feedType = "atom_1.0"
            id = "urn:uuid:${UUID.randomUUID()}"
            title = feedTitle
            otherLinks = listOfNotNull(
                    Link().apply { rel = "self"; href = "${baseHref}" },
                    if (!events.isFirst) Link().apply { rel = "prev-archive"; href = "${baseHref}/${events.number - 1}" } else null,
                    if (!events.isLast) Link().apply { rel = "next-archive"; href = "${baseHref}/${events.number + 1}" } else null
            )
            authors = listOf(Person().apply { name = feedAuthorName })
            updated = Date()
            entries = events.content.reversed().map { buildEntry(it) }
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