package com.realworld.springmongo.validation

import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import java.util.*

@Component
class LocaleConfigurer : ApplicationListener<ContextRefreshedEvent> {
    /**
     * Makes hibernate validation always use English default messages
     */
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        Locale.setDefault(Locale.ENGLISH)
    }
}