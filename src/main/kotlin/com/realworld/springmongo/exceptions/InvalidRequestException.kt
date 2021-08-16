package com.realworld.springmongo.exceptions

class InvalidRequestException(val subject: String, val violation: String) : RuntimeException("$subject: $violation")