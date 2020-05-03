/*
 * Copyright 2020 Tomasz Klaja
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.digitfaber.messaging;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;

/**
 * Documentation in progress, if you want to learn more about this class consider reading README.md
 *
 * @author Tomasz Klaja
 * @since 1.0.0
 */
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
