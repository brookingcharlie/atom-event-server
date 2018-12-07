# Atom Event Server

Simple Kotlin/Spring application serving a stream of events as an
[Atom](https://tools.ietf.org/html/rfc4287) feed.

Events are paged using archive documents as defined in
[RFC 5005: Feed Paging and Archiving](https://tools.ietf.org/html/rfc5005).

# Usage

To run the server:

    mvn spring-boot:run

To fetch the current feed document:

    curl http://localhost:8080/feed

This will return a document containing the most recent entries in the feed
and a link the immediately-preceding archive document.

    <feed xmlns="http://www.w3.org/2005/Atom">
      <title>Demo Feed</title>
      <link rel="self" href="http://localhost:8080/feed" />
      <link rel="prev-archive" href="http://localhost:8080/feed/2" />
      <author>
        <name>Demonstrations Inc</name>
      </author>
      <id>urn:uuid:df22d05e-8a4d-4f70-a02a-39e414cde85f</id>
      <updated>2018-01-01T00:00:10Z</updated>
      <entry>
        <title>Event 8b4f36b8-8298-448b-982e-dc3e6d1fc487</title>
        <id>urn:uuid:8b4f36b8-8298-448b-982e-dc3e6d1fc487</id>
        <updated>2018-01-01T00:00:10Z</updated>
        <content type="application/json">eyJ0aXRsZSI6IkV2ZW50IDhiNGYzNmI4LTgyOTgtNDQ4Yi05ODJlLWRjM2U2ZDFmYzQ4NyJ9</content>
        <summary type="text">Event ID 8b4f36b8-8298-448b-982e-dc3e6d1fc487</summary>
      </entry>
      <entry>
        <title>Event 9457b47d-6936-4c9a-810a-bbf8e841483a</title>
        <id>urn:uuid:9457b47d-6936-4c9a-810a-bbf8e841483a</id>
        <updated>2018-01-01T00:00:09Z</updated>
        <content type="application/json">eyJ0aXRsZSI6IkV2ZW50IDk0NTdiNDdkLTY5MzYtNGM5YS04MTBhLWJiZjhlODQxNDgzYSJ9</content>
        <summary type="text">Event ID 9457b47d-6936-4c9a-810a-bbf8e841483a</summary>
      </entry>
    </feed>

You can then fetch the previous archives:

    $ curl http://localhost:8080/feed/2
    <feed xmlns="http://www.w3.org/2005/Atom">
      <link rel="current" href="http://localhost:8080/feed" />
      <link rel="self" href="http://localhost:8080/feed/2" />
      <link rel="prev-archive" href="http://localhost:8080/feed/1" />
      <!-- ... -->
    </feed>

    $ curl http://localhost:8080/feed/1
    <feed xmlns="http://www.w3.org/2005/Atom">
      <title>Demo Feed</title>
      <link rel="self" href="http://localhost:8080/feed/1" />
      <link rel="next-archive" href="http://localhost:8080/feed/2" />
      <link rel="prev-archive" href="http://localhost:8080/feed/0" />
      <!-- ... -->
    </feed>

    $ curl http://localhost:8080/feed/0
    <feed xmlns="http://www.w3.org/2005/Atom">
      <link rel="current" href="http://localhost:8080/feed" />
      <link rel="self" href="http://localhost:8080/feed/0" />
      <link rel="next-archive" href="http://localhost:8080/feed/1" />
      <!-- ... -->
    </feed>
