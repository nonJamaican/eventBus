package com.example.core.event;


import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class CoreEventBusTest {

    private EventBus bus;

    @Before
    public void setUp() throws Exception {
        bus = new CoreEventBus();
    }

    @Test
    public void a_registered_object_can_receive_events() throws Exception {
        CountListener listener = new CountListener();
        bus.subscribe(listener);

        bus.post(new TestEvent());

        assertThat(listener.count, CoreMatchers.is(CoreMatchers.equalTo(1)));
    }

    @Test(expected = IllegalStateException.class)
    public void throws_exeption_if_more_that_one_argument_in_the_method_subscriber() {
        bus.subscribe(new TooManyArgumentsMethod());
    }

    private class CountListener {

        public int count = 0;

        @Subscribe
        public void on(TestEvent event) {
            count++;
        }

    }

    private class TestEvent {
    }

    private class TooManyArgumentsMethod {
        @Subscribe
        public void on(int a, boolean b) {
        }
    }
}