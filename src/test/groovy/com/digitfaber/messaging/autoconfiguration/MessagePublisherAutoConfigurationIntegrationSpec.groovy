package com.digitfaber.messaging.autoconfiguration

import com.digitfaber.messaging.MessagePublisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(classes = AutoConfigurationTestApplication)
class MessagePublisherAutoConfigurationIntegrationSpec extends Specification {

    @Autowired
    MessagePublisher messagePublisher

    def "should auto-configure MessagePublisher when no other MessagePublisher bean exists in the application context"() {
        when:
        "context loads"

        then:
        messagePublisher != null
    }

}