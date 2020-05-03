package com.digitfaber.messaging

import spock.lang.Specification

class MessagePublisherSpec extends Specification {

    def "should not throw exception when none of builder parameters are set"() {
        when:
        MessagePublisher.builder()
                .build()

        then:
        noExceptionThrown()
    }

}