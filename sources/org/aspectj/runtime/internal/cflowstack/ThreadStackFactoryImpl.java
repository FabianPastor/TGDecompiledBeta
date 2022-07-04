package org.aspectj.runtime.internal.cflowstack;

import java.util.Stack;

public class ThreadStackFactoryImpl implements ThreadStackFactory {

    private static class ThreadStackImpl extends ThreadLocal implements ThreadStack {
        private ThreadStackImpl() {
        }

        public Object initialValue() {
            return new Stack();
        }

        public Stack getThreadStack() {
            return (Stack) get();
        }

        public void removeThreadStack() {
            remove();
        }
    }

    public ThreadStack getNewThreadStack() {
        return new ThreadStackImpl();
    }

    private static class ThreadCounterImpl extends ThreadLocal implements ThreadCounter {
        private ThreadCounterImpl() {
        }

        public Object initialValue() {
            return new Counter();
        }

        public Counter getThreadCounter() {
            return (Counter) get();
        }

        public void removeThreadCounter() {
            remove();
        }

        public void inc() {
            getThreadCounter().value++;
        }

        public void dec() {
            Counter threadCounter = getThreadCounter();
            threadCounter.value--;
        }

        public boolean isNotZero() {
            return getThreadCounter().value != 0;
        }

        static class Counter {
            protected int value = 0;

            Counter() {
            }
        }
    }

    public ThreadCounter getNewThreadCounter() {
        return new ThreadCounterImpl();
    }
}
