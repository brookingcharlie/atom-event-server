package com.example.demo

import com.rometools.rome.io.WireFeedOutput
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.io.OutputStream
import java.io.PrintWriter
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

@Controller
class FeedController {
    @RequestMapping(method = [RequestMethod.GET], path = ["/feed"])
    @ResponseBody
    fun getFeed(out: OutputStream) {
        val feed = FeedBuilder().build(
                "http://localhost:8080/feed",
                "Demo Feed",
                "Demonstrations Inc",
                (0 until 10).map {
                    val eventId = UUID.randomUUID().toString()
                    val timestamp = OffsetDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).plusDays(it.toLong())
                    val payload = """{"foo":"Event ${eventId}"}"""
                    Event(eventId, timestamp, payload)
                }
        )
        val writer = PrintWriter(out, false, Charsets.UTF_8)
        WireFeedOutput().output(feed, writer)
    }
}