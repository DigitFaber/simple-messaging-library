package com.digitfaber.messaging

import spock.lang.Specification

class MultiExchangeMessagePublisherSpec extends Specification {

    def "should not throw exception when each builder parameter is not set"() {
        when:
        MultiExchangeMessagePublisher.builder()
                .build()

        then:
        noExceptionThrown()
    }

}