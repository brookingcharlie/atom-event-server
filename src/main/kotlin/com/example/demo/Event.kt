package com.example.demo

import java.time.OffsetDateTime

data class Event(val id: String, val timestamp: OffsetDateTime, val payload: String)