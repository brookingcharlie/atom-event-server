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
    @RequestMapping(method = [RequestMethod.GET], path = ["/feed"])
    @ResponseBody
    fun getFeed(out: OutputStream) {
        val baseHref = "http://localhost:8080/feed"

        val feed = Feed().apply {
            feedType = "atom_1.0"
            id = "${baseHref}/"
            title = "Demo Feed"
            otherLinks = listOf(Link().apply { rel = "self"; href = "${baseHref}/" })
            authors = listOf(Person().apply { name = "QTAC" })
            updated = Date()
        }

        val entry = Entry().apply {
            title = "ROME v1.0"
            id = "${baseHref}/entry-1"
            updated = GregorianCalendar(2004, 5, 8).time
            contents = listOf(Content().apply {
                type = "application/json"
                value = base64("""{"title":"Test"}""")
            })
            summary = Content().apply { type = "text/plain"; value = "Entry 1" }
        }

        feed.entries = listOf(entry)

        val writer = PrintWriter(out, false, Charsets.UTF_8)
        val output = WireFeedOutput()
        output.output(feed, writer)
    }

    private fun base64(s: String) = Base64.getEncoder().encodeToString(s.toByteArray(Charsets.UTF_8))
}