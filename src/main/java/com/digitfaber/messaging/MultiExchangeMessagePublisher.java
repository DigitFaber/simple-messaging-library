package com.digitfaber.messaging;

import java.time.Duration;
import java.util.concurrent.ThreadFactory;

public class MultiExchangeMessagePublisher extends MessagePublisherBase {

    public MultiExchangeMessagePublisher(Duration keepAliveTime,
                                         ThreadFactory threadFactory,
                                         Integer maximumPoolSize,
                                         Integer corePoolSize) {
        if (keepAliveTime != null) {
            super.setKeepAliveTimeInNanoseconds(keepAliveTime.toNanos());
        }
        if (threadFactory != null) {
            super.setThreadFactory(threadFactory);
        }
        if (maximumPoolSize != null) {
            super.setCorePoolSize(maximumPoolSize);
        }
        if (corePoolSize != null) {
            super.setCorePoolSize(corePoolSize);
        }
    }

    public void publish(String exchangeName, String messageName, Object message) {
        super.publish(exchangeName, messageName, message);
    }

    public void publishSynchronously(String exchangeName, String messageName, Object message) {
        super.publishSynchronously(exchangeName, messageName, message);
    }


    public static MultiExchangeMessagePublisherBuilder builder() {
        return new MultiExchangeMessagePublisherBuilder();
    }

    public static class MultiExchangeMessagePublisherBuilder {

        private Duration keepAliveTime;
        private ThreadFactory threadFactory;
        private Integer maximumPoolSize;
        private Integer corePoolSize;

        MultiExchangeMessagePublisherBuilder() {
        }

        public MultiExchangeMessagePublisherBuilder keepAliveTime(Duration keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
            return this;
        }

        public MultiExchangeMessagePublisherBuilder threadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        public MultiExchangeMessagePublisherBuilder maximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
            return this;
        }

        public MultiExchangeMessagePublisherBuilder corePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
            return this;
        }

        public MultiExchangeMessagePublisher build() {
            return new MultiExchangeMessagePublisher(
                    keepAliveTime,
                    threadFactory,
                    maximumPoolSize,
                    corePoolSize);
        }

    }

}
