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
import java.io.OutputStreamWriter

@Controller
class FeedController(@Autowired val eventRepository: EventRepository, @Autowired val feedBuilder: FeedBuilder) {
    companion object {
        private const val FEED_BASE_HREF = "http://localhost:8080/feed"
        private const val FEED_AUTHOR_NAME = "Demonstrations Inc"
        private const val FEED_TITLE = "Demo Feed"
        private const val PAGE_SIZE = 3
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/feed", "/feed/{page}"])
    @ResponseBody
    fun getFeed(out: OutputStream, @PathVariable page: Int?) {
        val impliedPage = page ?: (eventRepository.count() / PAGE_SIZE).toInt()
        val events = eventRepository.findAll(PageRequest.of(impliedPage, PAGE_SIZE))
        val feed = feedBuilder.build(FEED_BASE_HREF, FEED_TITLE, FEED_AUTHOR_NAME, events)
        WireFeedOutput().output(feed, OutputStreamWriter(out, Charsets.UTF_8))
    }
}