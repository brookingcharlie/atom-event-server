package com.example.demo

import com.rometools.rome.io.WireFeedOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import java.io.OutputStream
import java.io.PrintWriter

@Controller
class FeedController(@Autowired val eventRepository: EventRepository) {
    private val pageSize = 20

    @RequestMapping(method = [RequestMethod.GET], path = ["/feed", "/feed/{page}"])
    @ResponseBody
    fun getFeed(out: OutputStream, @PathVariable page: Int?) {
        val impliedPage = page ?: (eventRepository.count() / pageSize).toInt()
        val feed = FeedBuilder().build(
                "http://localhost:8080/feed",
                "Demo Feed",
                "Demonstrations Inc",
                eventRepository.findAll(PageRequest.of(impliedPage, pageSize))
        )
        val writer = PrintWriter(out, false, Charsets.UTF_8)
        WireFeedOutput().output(feed, writer)
    }
}