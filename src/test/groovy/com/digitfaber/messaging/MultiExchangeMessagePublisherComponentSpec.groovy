package com.digitfaber.messaging

import org.springframework.scheduling.concurrent.DefaultManagedAwareThreadFactory
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import java.time.Duration

import static com.digitfaber.messaging.TestSubscriber.*

class MultiExchangeMessagePublisherComponentSpec extends Specification {

    MultiExchangeMessagePublisher multiExchangeMessagePublisher

    TestSubscriber testSubscriber

    def setup() {
        multiExchangeMessagePublisher = MultiExchangeMessagePublisher.builder()
                .keepAliveTime(Duration.ofMillis(10))
                .threadFactory(new DefaultManagedAwareThreadFactory())
                .maximumPoolSize(10)
                .corePoolSize(1)
                .build()
    }

    def "should register subscriber"() {
        given:
        testSubscriber = new TestSubscriber()

        when:
        multiExchangeMessagePublisher.registerSubscriber(testSubscriber)

        then:
        multiExchangeMessagePublisher.getRegisteredSubscribers() == Set.of(testSubscriber)
    }

    def "should not deliver message when exchange name is incorrect"() {
        given:
        def initialMessageContent = ""
        def publishedMessage = "Message"
        def incorrectExchangeName = "NonexistentExchange"

        and:
        testSubscriber = new TestSubscriber(initialMessageContent)
        multiExchangeMessagePublisher.registerSubscriber(testSubscriber)

        when:
        multiExchangeMessagePublisher.publishSynchronously(incorrectExchangeName, MESSAGE_NAME, publishedMessage)

        then:
        testSubscriber.getMessageContent() == initialMessageContent
    }

    def "should deliver message to the annotated method in registered TestSubscriber using synchronous call"() {
        given:
        def initialMessageContent = ""
        def publishedMessage = "Message"

        and:
        testSubscriber = new TestSubscriber(initialMessageContent)
        multiExchangeMessagePublisher.registerSubscriber(testSubscriber)

        when:
        multiExchangeMessagePublisher.publishSynchronously(EXCHANGE_NAME, MESSAGE_NAME, publishedMessage)

        then:
        testSubscriber.getMessageContent() == publishedMessage
    }

    def "should deliver message to the annotated method in registered TestSubscriber using asynchronous call"() {
        given:
        int timeoutSeconds = 3
        def conditions = new PollingConditions(timeout: timeoutSeconds)

        and:
        def initialMessageContent = ""
        def publishedMessage = "Message"

        and:
        testSubscriber = new TestSubscriber(initialMessageContent)
        multiExchangeMessagePublisher.registerSubscriber(testSubscriber)

        when:
        multiExchangeMessagePublisher.publish(EXCHANGE_NAME, MESSAGE_NAME, publishedMessage)

        then:
        conditions.within(timeoutSeconds) {
            assert testSubscriber.getMessageContent() == publishedMessage
        }
    }

    def "should not deliver message to the annotated method in unregistered TestSubscriber using synchronous call"() {
        given:
        int timeoutSeconds = 3
        def conditions = new PollingConditions(timeout: timeoutSeconds)

        and:
        def initialMessageContent = ""
        def publishedMessage = "Message"

        and:
        testSubscriber = new TestSubscriber(initialMessageContent)
        multiExchangeMessagePublisher.registerSubscriber(testSubscriber)

        when:
        multiExchangeMessagePublisher.unregisterSubscriber(testSubscriber)

        and:
        multiExchangeMessagePublisher.publish(EXCHANGE_NAME, MESSAGE_NAME, publishedMessage)

        then:
        conditions.within(timeoutSeconds) {
            assert testSubscriber.getMessageContent() == initialMessageContent
        }
    }

    def "should not deliver message to the annotated method in unregistered TestSubscriber using asynchronous call"() {
        given:
        int timeoutSeconds = 3
        def conditions = new PollingConditions(timeout: timeoutSeconds)

        and:
        def initialMessageContent = ""
        def publishedMessage = "Message"

        and:
        testSubscriber = new TestSubscriber(initialMessageContent)
        multiExchangeMessagePublisher.registerSubscriber(testSubscriber)

        when:
        multiExchangeMessagePublisher.unregisterSubscriber(testSubscriber)

        and:
        multiExchangeMessagePublisher.publish(EXCHANGE_NAME, MESSAGE_NAME, publishedMessage)

        then:
        conditions.within(timeoutSeconds) {
            assert testSubscriber.getMessageContent() == initialMessageContent
        }
    }

    def "should not deliver message and log error when invocation error occurred"() {
        given:
        def initialMessageContent = ""
        def publishedMessage = "Message"

        and:
        testSubscriber = new TestSubscriber(initialMessageContent)
        multiExchangeMessagePublisher.registerSubscriber(testSubscriber)

        when:
        multiExchangeMessagePublisher.publishSynchronously(EXCHANGE_NAME, INCORRECT_LISTENER_MESSAGE_NAME, publishedMessage)

        then:
        testSubscriber.getMessageContent() == initialMessageContent
    }

}