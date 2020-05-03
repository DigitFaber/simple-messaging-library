package com.digitfaber.messaging

import org.springframework.scheduling.concurrent.DefaultManagedAwareThreadFactory
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

import java.time.Duration

import static com.digitfaber.messaging.TestSubscriber.*

class MessagePublisherComponentSpec extends Specification {

    MessagePublisher messagePublisher

    TestSubscriber testSubscriber

    def setup() {
        messagePublisher = MessagePublisher.builder()
                .exchangeName(EXCHANGE_NAME)
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
        messagePublisher.registerSubscriber(testSubscriber)

        then:
        messagePublisher.getRegisteredSubscribers() == Set.of(testSubscriber)
    }

    def "should deliver message to the annotated method in registered TestSubscriber using private listener method"() {
        given:
        def initialMessageContent = ""
        def publishedMessage = "Message"

        and:
        testSubscriber = new TestSubscriber(initialMessageContent)
        messagePublisher.registerSubscriber(testSubscriber)

        when:
        messagePublisher.publishSynchronously(PRIVATE_LISTENER_MESSAGE_NAME, publishedMessage)

        then:
        testSubscriber.getMessageContent() == publishedMessage
    }

    def "should deliver message to the annotated method in registered TestSubscriber using synchronous call"() {
        given:
        def initialMessageContent = ""
        def publishedMessage = "Message"

        and:
        testSubscriber = new TestSubscriber(initialMessageContent)
        messagePublisher.registerSubscriber(testSubscriber)

        when:
        messagePublisher.publishSynchronously(MESSAGE_NAME, publishedMessage)

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
        messagePublisher.registerSubscriber(testSubscriber)

        when:
        messagePublisher.publish(MESSAGE_NAME, publishedMessage)

        then:
        conditions.within(timeoutSeconds){
            assert testSubscriber.getMessageContent() == publishedMessage
        }
    }

    def "should not deliver message to the annotated method in deregistered TestSubscriber using synchronous call"() {
        given:
        int timeoutSeconds = 3
        def conditions = new PollingConditions(timeout: timeoutSeconds)

        and:
        def initialMessageContent = ""
        def publishedMessage = "Message"

        and:
        testSubscriber = new TestSubscriber(initialMessageContent)
        messagePublisher.registerSubscriber(testSubscriber)

        when:
        messagePublisher.deregisterSubscriber(testSubscriber)
        
        and:
        messagePublisher.publish(MESSAGE_NAME, publishedMessage)

        then:
        conditions.within(timeoutSeconds){
            assert testSubscriber.getMessageContent() == initialMessageContent
        }
    }

    def "should not deliver message to the annotated method in deregistered TestSubscriber using asynchronous call"() {
        given:
        int timeoutSeconds = 3
        def conditions = new PollingConditions(timeout: timeoutSeconds)

        and:
        def initialMessageContent = ""
        def publishedMessage = "Message"

        and:
        testSubscriber = new TestSubscriber(initialMessageContent)
        messagePublisher.registerSubscriber(testSubscriber)

        when:
        messagePublisher.deregisterSubscriber(testSubscriber)

        and:
        messagePublisher.publish(MESSAGE_NAME, publishedMessage)

        then:
        conditions.within(timeoutSeconds){
            assert testSubscriber.getMessageContent() == initialMessageContent
        }
    }

    def "should not deliver message and log error when invocation error occurred"() {
        given:
        def initialMessageContent = ""
        def publishedMessage = "Message"

        and:
        testSubscriber = new TestSubscriber(initialMessageContent)
        messagePublisher.registerSubscriber(testSubscriber)

        when:
        messagePublisher.publishSynchronously(INCORRECT_LISTENER_MESSAGE_NAME, publishedMessage)

        then:
        testSubscriber.getMessageContent() == initialMessageContent
    }

}