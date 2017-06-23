package com.example.core.event;


public interface EventBus {

    void subscribe(Object subscriber);

    void unSubscribe(Object subscriber);

    void post(Object event);

}
