package com.realworld.springmongo.article.repository

import com.realworld.springmongo.article.Tag
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest

@DataMongoTest
class TagRepositoryTest(
    @Autowired val tagRepository: TagRepository,
) {
    @Test
    fun name() {
        val tags = listOf("tag1", "tag1", "tag2", "tag2", "tag3")
        val expectedTags = mutableSetOf("tag1", "tag2", "tag3")
        val returnedTags = tagRepository.saveAllTags(tags).block()!!.map(Tag::tagName)
        val allTags = tagRepository.findAll().map(Tag::tagName).collectList().block()!!
        assertThat(returnedTags.toSet()).isEqualTo(expectedTags)
        assertThat(allTags.toSet()).isEqualTo(expectedTags)
    }
}