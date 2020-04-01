package com.example.demo

import com.rometools.rome.feed.atom.Category
import com.rometools.rome.feed.atom.Content
import com.rometools.rome.feed.atom.Entry
import com.rometools.rome.feed.atom.Feed
import com.rometools.rome.feed.atom.Link
import com.rometools.rome.feed.atom.Person
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.time.Instant
import java.time.OffsetDateTime
import java.util.Base64
import java.util.Date
import java.util.UUID
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart


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
        val multipart = buildMultipartContent(event)
        val multipartContentType: String = multipart.contentType.replace("\r\n\t", "")
        val multipartContentBytes: ByteArray = ByteArrayOutputStream().also { multipart.writeTo(it) }.toByteArray()

        return Entry().apply {
            title = "Event ${event.id}"
            id = "urn:uuid:${event.id}"
            updated = date(event.timestamp)
            categories = listOf(Category().apply {
                scheme = "http://example.org/categories/events/type"
                term = "Something"
            })
            contents = listOf(Content().apply {
                type = multipartContentType
                value = base64(multipartContentBytes)
            })
            summary = Content().apply { type = "text/plain"; value = "Event ID ${event.id}" }
        }
    }

    private fun buildMultipartContent(event: Event): MimeMultipart {
        val multipart = MimeMultipart("alternative")
        multipart.addBodyPart(MimeBodyPart().apply {
            setContent(event.payload, "text/plain; charset=utf-8")
            setHeader("Content-Type", "application/vnd.example.something-v1+json; charset=utf-8")
        })
        multipart.addBodyPart(MimeBodyPart().apply {
            setContent("""{"title":"Made up event to show second version"}""", "text/plain; charset=utf-8")
            setHeader("Content-Type", "application/vnd.example.something-v2+json; charset=utf-8")
        })
        return multipart
    }

    private fun date(offsetDateTime: OffsetDateTime) = Date.from(offsetDateTime.toInstant());
    private fun base64(s: ByteArray) = Base64.getEncoder().encodeToString(s)
}