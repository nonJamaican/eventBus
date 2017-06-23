package com.example.core.event;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CoreEventBus implements EventBus {

    private Map<Object, List<MethodSubscription>> subscriptions = new HashMap();

    interface MyEventHandler {
        void invoke(Object runnable);
    }

    class MethodSubscription {
        private final Object subscriber;
        private final MyEventHandler handler;

        MethodSubscription(Object subscriber, MyEventHandler handler) {
            this.subscriber = subscriber;
            this.handler = handler;
        }

    }


    @Override
    public void subscribe(final Object subscriber) {
        Method[] declaredMethods = subscriber.getClass().getDeclaredMethods();

        List<Method> subscribeAnnotatedMethods = new ArrayList<>();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.isAnnotationPresent(Subscribe.class)) {
                subscribeAnnotatedMethods.add(declaredMethod);
            }
        }

        for (final Method method : subscribeAnnotatedMethods) {
            if (method.getParameterCount() != 1) {
                throw new IllegalStateException("method ${subscriber::class.simpleName}#${method.name} should only have one parameter");
            }

            Parameter parameter = method.getParameters()[0];
            Class<?> type = parameter.getType();
            subscriptions.put(type, new ArrayList<MethodSubscription>());

            List<MethodSubscription> methodSubscriptions = subscriptions.get(type);

            MyEventHandler handler = new MyEventHandler() {
                @Override
                public void invoke(Object runnable) {
                    try {
                        method.invoke(subscriber, runnable);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            };
            methodSubscriptions.add(new MethodSubscription(subscriber, handler));

        }
    }

    @Override
    public void unSubscribe(Object subscriber) {

    }

    @Override
    public void post(Object event) {
        Class<?> key = event.getClass();

        if (subscriptions.containsKey(key)) {
            List<MethodSubscription> receivers = subscriptions.get(key);
            for (MethodSubscription receiver : receivers) {
                receiver.handler.invoke(event);
            }
        }
    }
}