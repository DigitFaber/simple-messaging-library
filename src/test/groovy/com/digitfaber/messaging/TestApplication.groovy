package com.digitfaber.messaging

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.DefaultManagedAwareThreadFactory

import java.time.Duration

import static com.digitfaber.messaging.TestSubscriber.EXCHANGE_NAME

@SpringBootApplication
class TestApplication {

    static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args)
    }

    @Configuration
    class TestConfiguration {

        @Bean
        MessagePublisher messagePublisher() {
            MessagePublisher.builder()
                    .exchangeName(EXCHANGE_NAME)
                    .keepAliveTime(Duration.ofMillis(10))
                    .threadFactory(new DefaultManagedAwareThreadFactory())
                    .maximumPoolSize(10)
                    .corePoolSize(1)
                    .build()
        }

        @Bean
        MultiExchangeMessagePublisher multiExchangeMessagePublisher() {
            MultiExchangeMessagePublisher.builder()
                    .keepAliveTime(Duration.ofMillis(10))
                    .threadFactory(new DefaultManagedAwareThreadFactory())
                    .maximumPoolSize(10)
                    .corePoolSize(1)
                    .build()
        }

        @Bean
        TestSubscriber testSubscriber() {
            new TestSubscriber("")
        }

    }

}