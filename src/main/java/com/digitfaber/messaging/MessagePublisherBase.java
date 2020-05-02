package com.digitfaber.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class MessagePublisherBase {

    private static final Logger log = Logger.getLogger(MessagePublisherBase.class.getName());

    private final ThreadPoolExecutor executor;
    private final Set<Object> subscribers;
    private ApplicationContext applicationContext;

    protected MessagePublisherBase() {
        this.subscribers = new HashSet<>();
        this.executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    @Autowired
    private void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    protected void setKeepAliveTimeInNanoseconds(long timeInNanoseconds) {
        executor.setKeepAliveTime(timeInNanoseconds, TimeUnit.NANOSECONDS);
    }

    protected void setThreadFactory(ThreadFactory threadFactory) {
        executor.setThreadFactory(threadFactory);
    }

    protected void setMaximumPoolSize(int maximumPoolSize) {
        executor.setMaximumPoolSize(maximumPoolSize);
    }

    protected void setCorePoolSize(int corePoolSize) {
        executor.setCorePoolSize(corePoolSize);
    }

    public void registerSubscriber(Object subscriber) {
        subscribers.add(subscriber);
    }

    public void unregisterSubscriber(Object subscriber) {
        subscribers.remove(subscriber);
    }

    public Set<Object> getRegisteredSubscribers() {
        return Set.copyOf(subscribers);
    }

    protected void publish(String exchangeName, String messageName, Object message) {
        executor.submit(() -> publishSynchronously(exchangeName, messageName, message));
    }

    protected void publishSynchronously(String exchangeName, String messageName, Object message) {
        if (applicationContext != null) {
            publishToSpringSubscribers(exchangeName, messageName, message);
        }
        publishToRegisteredSubscribers(exchangeName, messageName, message);
    }

    private void publishToSpringSubscribers(String exchangeName, String messageName, Object message) {
        List.of(applicationContext.getBeanDefinitionNames()).forEach(beanName -> {
            Object bean = applicationContext.getBean(beanName);
            getAllAnnotatedMethods(bean).forEach(method ->
                    invoke(bean, method, exchangeName, messageName, message));
        });
    }

    private void publishToRegisteredSubscribers(String exchangeName, String messageName, Object message) {
        subscribers.forEach(subscriber ->
                getAllAnnotatedMethods(subscriber).forEach(method ->
                        invoke(subscriber, method, exchangeName, messageName, message)));
    }

    private void invoke(Object object, Method method, String exchangeName, String messageName, Object message) {
        MessageListener annotation = method.getAnnotation(MessageListener.class);
        if (annotation.message().equals(messageName) && annotation.exchange().equals(exchangeName)) {
            try {
                if (!method.canAccess(object)) {
                    method.trySetAccessible();
                }
                method.invoke(object, message);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exception) {
                log.log(Level.SEVERE, "Error occurred during method invocation - " +
                        "method: " + method.getName() + ", " +
                        "object class: " + object.getClass().getName() + ", " +
                        "reason: " + exception.getMessage());
            }
        }
    }

    private List<Method> getAllAnnotatedMethods(Object subscriber) {
        List<Method> methods = new ArrayList<>();
        Class<?> subscriberClass = subscriber.getClass();
        while (haveSuperclass(subscriberClass)) {
            for (Method method : subscriberClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(MessageListener.class)) {
                    methods.add(method);
                }
            }
            subscriberClass = subscriberClass.getSuperclass();
        }
        return methods;
    }

    private boolean haveSuperclass(Class<?> subscriberClass) {
        return subscriberClass != Object.class;
    }

}
