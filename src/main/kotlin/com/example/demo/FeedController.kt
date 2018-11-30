package com.example.demo

import com.rometools.rome.feed.atom.*
import com.rometools.rome.io.WireFeedOutput
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.io.OutputStream
import java.io.PrintWriter
import java.util.*


@Controller
class FeedController {
    private val baseHref = "http://localhost:8080/feed"

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
            title = "Demo Feed"
            otherLinks = listOf(Link().apply { rel = "self"; href = "${baseHref}/" })
            authors = listOf(Person().apply { name = "QTAC" })
            updated = Date()
            entries = (1..5).map { buildEntry(it) }
        }
    }

    private fun buildEntry(entryId: Int): Entry {
        return Entry().apply {
            title = "ROME v1.0"
            id = "${baseHref}/${entryId}"
            updated = GregorianCalendar(2004, 5, 8).time
            contents = listOf(Content().apply {
                type = "application/json"
                value = base64("""{"title":"Test"}""")
            })
            summary = Content().apply { type = "text/plain"; value = "Entry ${entryId}" }
        }
    }

    private fun base64(s: String) = Base64.getEncoder().encodeToString(s.toByteArray(Charsets.UTF_8))
}