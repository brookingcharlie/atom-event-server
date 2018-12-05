package com.example.demo

import com.rometools.rome.feed.atom.Content
import com.rometools.rome.feed.atom.Entry
import com.rometools.rome.feed.atom.Feed
import com.rometools.rome.feed.atom.Link
import com.rometools.rome.feed.atom.Person
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.OffsetDateTime
import java.util.Base64
import java.util.Date
import java.util.UUID

@Service
class FeedBuilder {
    fun build(baseHref: String, feedTitle: String, feedAuthorName: String, page: Page<Event>): Feed {
        return Feed().apply {
            feedType = "atom_1.0"
            id = "urn:uuid:${UUID.randomUUID()}"
            title = feedTitle
            otherLinks = buildOtherLinks(baseHref, page)
            authors = listOf(Person().apply { name = feedAuthorName })
            updated = if (!page.isEmpty) date(page.content.last().timestamp) else Date.from(Instant.EPOCH)
            entries = page.content.reversed().map { buildEntry(it) }
        }
    }

    private fun buildOtherLinks(baseHref: String, page: Page<Event>): List<Link> {
        val isSubscription = page.isLast
        val hasNextArchive = page.number < page.totalPages - 2
        val hasPrevArchive = !page.isFirst
        return listOfNotNull(
                if (isSubscription) Link().apply { rel = "self"; href = "${baseHref}" } else null,
                if (!isSubscription) Link().apply { rel = "current"; href = "${baseHref}" } else null,
                if (!isSubscription) Link().apply { rel = "self"; href = "${baseHref}/${page.number}"} else null,
                if (hasNextArchive) Link().apply { rel = "next-archive"; href = "${baseHref}/${page.number + 1}" } else null,
                if (hasPrevArchive) Link().apply { rel = "prev-archive"; href = "${baseHref}/${page.number - 1}" } else null
        )
    }

    private fun buildEntry(event: Event): Entry {
        return Entry().apply {
            title = "Event ${event.id}"
            id = "urn:uuid:${event.id}"
            updated = date(event.timestamp)
            contents = listOf(Content().apply { type = "application/json"; value = base64(event.payload) })
            summary = Content().apply { type = "text/plain"; value = "Event ID ${event.id}" }
        }
    }

    private fun date(offsetDateTime: OffsetDateTime) = Date.from(offsetDateTime.toInstant());
    private fun base64(s: String) = Base64.getEncoder().encodeToString(s.toByteArray(Charsets.UTF_8))
}