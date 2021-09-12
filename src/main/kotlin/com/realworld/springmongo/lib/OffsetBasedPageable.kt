package com.realworld.springmongo.lib

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class OffsetBasedPageable(
    private val limit: Int,
    private val offset: Long,
    private val sort: Sort = Sort.unsorted(),
) : Pageable {

    init {
        require(limit >= 1) { "Limit must not be less than one" }
        require(offset >= 0) { "Offset index must not be less than zero" }
    }

    override fun getPageSize(): Int = limit

    override fun getOffset(): Long = offset

    override fun getSort(): Sort = sort

    override fun getPageNumber(): Int = unsupportedOperation()

    override fun next(): Pageable = unsupportedOperation()

    override fun previousOrFirst(): Pageable = unsupportedOperation()

    override fun first(): Pageable = unsupportedOperation()

    override fun withPage(pageNumber: Int): Pageable = unsupportedOperation()

    override fun hasPrevious(): Boolean = unsupportedOperation()

    private fun <T> unsupportedOperation(): T {
        throw UnsupportedOperationException("OffsetBasedPageable has no pages. Contains only offset and page size")
    }
}