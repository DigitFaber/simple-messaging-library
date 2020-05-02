package com.digitfaber.messaging

class TestSubscriber {

    static final EXCHANGE_NAME = "test"
    static final MESSAGE_NAME = "Test"
    static final PRIVATE_LISTENER_MESSAGE_NAME = "PrivateTest"
    static final INCORRECT_LISTENER_MESSAGE_NAME = "ErrorTest"

    String messageContent

    TestSubscriber(String initialMessageContent) {
        messageContent = initialMessageContent
    }

    @MessageListener(exchange = "test", message = "Test")
    public void messageListener(String message) {
        this.messageContent = message
    }

    @MessageListener(exchange = "test", message = "PrivateTest")
    private void privateMessageListener(String message) {
        this.messageContent = message
    }

    @MessageListener(exchange = "test", message = "ErrorTest")
    private void incorrectMessageListener(List message) {
        this.messageContent = message
    }

}
