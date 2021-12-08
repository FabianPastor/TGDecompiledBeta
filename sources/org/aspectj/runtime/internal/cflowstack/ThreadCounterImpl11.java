package org.aspectj.runtime.internal.cflowstack;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class ThreadCounterImpl11 implements ThreadCounter {
    private static final int COLLECT_AT = 20000;
    private static final int MIN_COLLECT_AT = 100;
    private Counter cached_counter;
    private Thread cached_thread;
    private int change_count = 0;
    private Hashtable counters = new Hashtable();

    static class Counter {
        protected int value = 0;

        Counter() {
        }
    }

    private synchronized Counter getThreadCounter() {
        if (Thread.currentThread() != this.cached_thread) {
            Thread currentThread = Thread.currentThread();
            this.cached_thread = currentThread;
            Counter counter = (Counter) this.counters.get(currentThread);
            this.cached_counter = counter;
            if (counter == null) {
                Counter counter2 = new Counter();
                this.cached_counter = counter2;
                this.counters.put(this.cached_thread, counter2);
            }
            this.change_count++;
            if (this.change_count > Math.max(100, 20000 / Math.max(1, this.counters.size()))) {
                List<Thread> dead_stacks = new ArrayList<>();
                Enumeration e = this.counters.keys();
                while (e.hasMoreElements()) {
                    Thread t = (Thread) e.nextElement();
                    if (!t.isAlive()) {
                        dead_stacks.add(t);
                    }
                }
                for (Thread t2 : dead_stacks) {
                    this.counters.remove(t2);
                }
                this.change_count = 0;
            }
        }
        return this.cached_counter;
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

    public void removeThreadCounter() {
    }
}
