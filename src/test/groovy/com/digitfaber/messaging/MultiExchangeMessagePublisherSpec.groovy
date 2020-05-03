package com.digitfaber.messaging

import spock.lang.Specification

class MultiExchangeMessagePublisherSpec extends Specification {

    def "should not throw exception when none of builder parameters are set"() {
        when:
        MultiExchangeMessagePublisher.builder()
                .build()

        then:
        noExceptionThrown()
    }

}