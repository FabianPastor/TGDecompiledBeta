package org.aspectj.runtime.internal.cflowstack;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

public class ThreadStackImpl11 implements ThreadStack {
    private static final int COLLECT_AT = 20000;
    private static final int MIN_COLLECT_AT = 100;
    private Stack cached_stack;
    private Thread cached_thread;
    private int change_count = 0;
    private Hashtable stacks = new Hashtable();

    public synchronized Stack getThreadStack() {
        if (Thread.currentThread() != this.cached_thread) {
            Thread currentThread = Thread.currentThread();
            this.cached_thread = currentThread;
            Stack stack = (Stack) this.stacks.get(currentThread);
            this.cached_stack = stack;
            if (stack == null) {
                Stack stack2 = new Stack();
                this.cached_stack = stack2;
                this.stacks.put(this.cached_thread, stack2);
            }
            this.change_count++;
            if (this.change_count > Math.max(100, 20000 / Math.max(1, this.stacks.size()))) {
                Stack dead_stacks = new Stack();
                Enumeration e = this.stacks.keys();
                while (e.hasMoreElements()) {
                    Thread t = (Thread) e.nextElement();
                    if (!t.isAlive()) {
                        dead_stacks.push(t);
                    }
                }
                Enumeration e2 = dead_stacks.elements();
                while (e2.hasMoreElements()) {
                    this.stacks.remove((Thread) e2.nextElement());
                }
                this.change_count = 0;
            }
        }
        return this.cached_stack;
    }

    public void removeThreadStack() {
    }
}
