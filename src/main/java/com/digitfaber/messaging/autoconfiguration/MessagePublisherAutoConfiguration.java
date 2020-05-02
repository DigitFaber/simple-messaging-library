package com.digitfaber.messaging.autoconfiguration;

import com.digitfaber.messaging.MessagePublisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagePublisherAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(type = "com.digitfaber.messaging.MessagePublisher")
    MessagePublisher messagePublisher(){
        return new MessagePublisher();
    }

}
