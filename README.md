# Simple Messaging Library

[![Build Status](https://travis-ci.com/DigitFaber/simple-messaging-library.svg?branch=master)](https://travis-ci.com/DigitFaber/simple-messaging-library) 
[![codecov](https://codecov.io/gh/DigitFaber/simple-messaging-library/branch/master/graph/badge.svg)](https://codecov.io/gh/DigitFaber/simple-messaging-library)

Simple messaging library dedicated mainly to small/new projects based on Spring Boot.

This library is my implementation of the Pub/Sub pattern.

## Dictionary
**_Message_** - the information you want to publish to interested units,
i.e. it is simply an object (of any class), it can be for example a domain event.

**_Publisher_** - the unit which generates the **_messages_** and sends them to 
interested units (**_subscribers_**). Classes `MessagePublisher` and `MultiExchangeMessagePublisher`.

**_Subscribers_** - the unit which is interested in receiving the **_messages_**. 
It could be an object of any class, but to receive a **_message_** it must have at least one method
annotated with `@MessageListener` annotation to listen for it.

## Quickstart

##### Step 1: Prepare message publisher

###### Spring
Create `MessagePublisher` bean.

Example:
```
@Bean
public MessagePublisher messagePublisher() {
    return new MessagePublisher("exchange");
}
```

###### Spring Boot
You can skip this step, because the library includes auto-configuration. 
If you don't create your own `MessagePublisher` bean, it will create one with `default` exchange for you!

###### Plain Java
Create `MessagePublisher` object:

Example: 
```
MessagePublisher messagePublisher = new MessagePublisher("default");
```

##### Step 2: Prepare subscriber

Create listener method and add `@MessageListener`.

Example: 
```
@MessageListener(exchange = "default", message = "Message")
private void subscribe(String message) {
    System.out.println(message);
}
```
(in this example, it is a method in the `SomeSubscriber` class)

##### Step 3: Prepare subscriber

###### Spring / Spring Boot
If your subscriber class is a `@Component`, `@Service`, etc. you don't have to do anything in this step (and that's
why I created this library :wink:).
Otherwise, you have to register your subscriber like in plain Java.

###### Plain Java
Create a subscriber object and register it in `MessagePublisher` using `registerSubscriber(...)` method.

Example:
```
SomeSubscriber subscriber = new SomeSubscriber() 
messagePublisher.registerSubscriber(subscriber);
```

##### Step 4: Publish a message
You are good to go! The last thing you have to do is make sure everything works just publishing a message.

Example:
```
messagePublisher.publish("Message", "This is some very important message. Hello, World BTW!"));
```

If something isn't working for you, let's check example projects which you can find [here](https://github.com/DigitFaber/simple-messaging-library-examples).

## Quick overview
Available classes/annotations:
   * **`MessagePublisher`** - allows publishing messages. 
      Message can be published on defined exchange (`default` if no exchange name provided).
      You can also set a few properties of internally used ThreadPoolExecutor using provided `Builder`.
      
      Most important methods:
      * `publish(...)` - publish a message with given name asynchronously (suggested way of publishing messages).
      * `publishSynchronously(...)` - publish a massage with given name synchronously.
      * `registerSubscriber(...)` - register a subscriber (intended for non Spring applications).
      * `deregisterSubscriber(...)` - deregister a subscriber (intended for non Spring applications).
      
   * **`MultiExchangeMessagePublisher`** - basically the same thing as **`MessagePublisher`**, but instead
      of defining exchange as constructor/builder parameter you have to specify exchange during message publishing.
      
      Most important methods:
      * `publish(...)` - publish a message with given name asynchronously (suggested way of publishing messages).
      * `publishSynchronously(...)` - publish a massage with given name synchronously.
      * `registerSubscriber(...)` - register a subscriber (intended for non Spring applications).
      * `deregisterSubscriber(...)` - deregister a subscriber (intended for non Spring applications).
      
      **IMPORTANT** - `MessagePublisher` and `MultiExchangeMessagePublisher` extends the same `MessagePublisherBase` 
      class to avoid code duplication, because they share basic functionality though publishing methods are different
      ("interfaces" are incompatible), so they are not meant to be casted to this basic class, so that's why it is 
      package-private.
      
   * **`@MessageListener`** - allows the publisher to detect the subscriber's listener method.
      To listen to a specific message, the method must have this annotation. 
      Furthermore, the method must have one and only one parameter of the message type sent (or any of its 
      superclasses).
      You can also create a generic message listener if you use the `Object` type.
                                            
      Available elements:      
      * `exchange` - communication "channel"
      * `message` - name of message
      
      Subscriber receives a message only if values of the annotation elements are the same as those used by 
      the publisher.
      Exchange just give's you "isolation" level of granulation if you want to for example make "UserCreated" message 
      (event) for "user" and "subscription" component (I mean different messages). 
      You can also just use the `default` (or whatever you want) exchange for both and make "user.UserCreated" and 
      "subscription.UserCreated" messages but let's be honest, this is not the prettiest solution. 
      Keep in mind that you will need separate instance of `MessagePublisher` for each exchange.

If you have any comments, suggestions, questions, etc. let me know on my 
[Linkedin](https://pl.linkedin.com/in/tomasz-klaja-44066216a) account.