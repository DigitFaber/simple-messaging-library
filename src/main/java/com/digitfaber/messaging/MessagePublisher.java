package com.digitfaber.messaging;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;

public class MessagePublisher extends MessagePublisherBase {

    private static final String DEFAULT_EXCHANGE = "default";

    private final String exchangeName;

    public MessagePublisher() {
        this(DEFAULT_EXCHANGE);
    }

    public MessagePublisher(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public MessagePublisher(String exchangeName,
                            Duration keepAliveTime,
                            ThreadFactory threadFactory,
                            Integer maximumPoolSize,
                            Integer corePoolSize) {
        this.exchangeName = Objects.requireNonNullElse(exchangeName, DEFAULT_EXCHANGE);
        if (keepAliveTime != null) {
            super.setKeepAliveTimeInNanoseconds(keepAliveTime.toNanos());
        }
        if (threadFactory != null) {
            super.setThreadFactory(threadFactory);
        }
        if (maximumPoolSize != null) {
            super.setMaximumPoolSize(maximumPoolSize);
        }
        if (corePoolSize != null) {
            super.setCorePoolSize(corePoolSize);
        }
    }

    public void publish(String messageName, Object message) {
        super.publish(exchangeName, messageName, message);
    }

    public void publishSynchronously(String messageName, Object message) {
        super.publishSynchronously(exchangeName, messageName, message);
    }


    public static MessagePublisherBuilder builder() {
        return new MessagePublisherBuilder();
    }

    public static class MessagePublisherBuilder {

        private String exchangeName;
        private Duration keepAliveTime;
        private ThreadFactory threadFactory;
        private Integer maximumPoolSize;
        private Integer corePoolSize;

        MessagePublisherBuilder() {
        }

        public MessagePublisherBuilder exchangeName(String exchangeName) {
            this.exchangeName = exchangeName;
            return this;
        }

        public MessagePublisherBuilder keepAliveTime(Duration keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
            return this;
        }

        public MessagePublisherBuilder threadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        public MessagePublisherBuilder maximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
            return this;
        }

        public MessagePublisherBuilder corePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
            return this;
        }

        public MessagePublisher build() {
            return new MessagePublisher(
                    exchangeName,
                    keepAliveTime,
                    threadFactory,
                    maximumPoolSize,
                    corePoolSize);
        }

    }

}
