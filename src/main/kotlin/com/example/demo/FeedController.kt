package com.example.demo

import com.rometools.rome.feed.atom.Content
import com.rometools.rome.feed.atom.Entry
import com.rometools.rome.feed.atom.Feed
import com.rometools.rome.feed.atom.Link
import com.rometools.rome.feed.atom.Person
import com.rometools.rome.io.WireFeedOutput
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.io.OutputStream
import java.io.PrintWriter
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Base64
import java.util.Date
import java.util.UUID

@Controller
class FeedController {
    private val baseHref = "http://localhost:8080/feed"
    private val feedTitle = "Demo Feed"
    private val feedAuthorName = "Demonstrations Inc"

    private data class Event(val id: String, val timestamp: OffsetDateTime, val payload: String)
    private val events: List<Event> = (0 until 10).map {
        val eventId = UUID.randomUUID().toString()
        val timestamp = OffsetDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).plusDays(it.toLong())
        val payload = """{"foo":"Event ${eventId}"}"""
        Event(eventId, timestamp, payload)
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/feed"])
    @ResponseBody
    fun getFeed(out: OutputStream) {
        val feed = buildFeed()
        val writer = PrintWriter(out, false, Charsets.UTF_8)
        WireFeedOutput().output(feed, writer)
    }

    private fun buildFeed(): Feed {
        return Feed().apply {
            feedType = "atom_1.0"
            id = "${baseHref}/"
            title = feedTitle
            otherLinks = listOf(Link().apply { rel = "self"; href = "${baseHref}/" })
            authors = listOf(Person().apply { name = feedAuthorName })
            updated = Date()
            entries = events.map { buildEntry(it) }
        }
    }

    private fun buildEntry(event: Event): Entry {
        return Entry().apply {
            title = "Event ${event.id}"
            id = "${baseHref}/${event.id}"
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