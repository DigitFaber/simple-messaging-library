package com.digitfaber.messaging

import spock.lang.Specification

class MessagePublisherSpec extends Specification {

    def "should not throw exception when each builder parameter is not set"() {
        when:
        MessagePublisher.builder()
                .build()

        then:
        noExceptionThrown()
    }

}