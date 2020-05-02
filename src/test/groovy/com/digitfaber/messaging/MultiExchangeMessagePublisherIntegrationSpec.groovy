package com.digitfaber.messaging

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import static com.digitfaber.messaging.TestSubscriber.*

@SpringBootTest(classes = TestApplication)
class MultiExchangeMessagePublisherIntegrationSpec extends Specification {

    @Autowired
    ApplicationContext applicationContext

    @Autowired
    MultiExchangeMessagePublisher multiExchangeMessagePublisher

    @Autowired
    TestSubscriber testSubscriber

    private static final INITIAL_MESSAGE_CONTENT = ""

    def setup() {
        testSubscriber.setMessageContent(INITIAL_MESSAGE_CONTENT)
    }

    def "should deliver message to the annotated method in TestSubscriber using synchronous call"() {
        given:
        def publishedMessage = "Message"

        when:
        multiExchangeMessagePublisher.publishSynchronously(EXCHANGE_NAME, MESSAGE_NAME, publishedMessage)

        then:
        testSubscriber.getMessageContent() == publishedMessage
    }

    def "should deliver message to the annotated method in TestSubscriber using asynchronous call"() {
        given:
        int timeoutSeconds = 3
        def conditions = new PollingConditions(timeout: timeoutSeconds)

        and:
        def publishedMessage = "Message"

        when:
        multiExchangeMessagePublisher.publish(EXCHANGE_NAME, MESSAGE_NAME, publishedMessage)

        then:
        conditions.within(timeoutSeconds) {
            assert testSubscriber.getMessageContent() == publishedMessage
        }
    }

    def "should not deliver message and log error when invocation error occurred"() {
        given:
        def publishedMessage = "Message"

        when:
        multiExchangeMessagePublisher.publishSynchronously(EXCHANGE_NAME, INCORRECT_LISTENER_MESSAGE_NAME, publishedMessage)

        then:
        testSubscriber.getMessageContent() == INITIAL_MESSAGE_CONTENT
    }

}