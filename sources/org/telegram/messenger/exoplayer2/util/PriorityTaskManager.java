package org.telegram.messenger.exoplayer2.util;

import java.io.IOException;
import java.util.Collections;
import java.util.PriorityQueue;

public final class PriorityTaskManager {
    private int highestPriority = Integer.MIN_VALUE;
    private final Object lock = new Object();
    private final PriorityQueue<Integer> queue = new PriorityQueue(10, Collections.reverseOrder());

    public static class PriorityTooLowException extends IOException {
        public PriorityTooLowException(int i, int i2) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Priority too low [priority=");
            stringBuilder.append(i);
            stringBuilder.append(", highest=");
            stringBuilder.append(i2);
            stringBuilder.append("]");
            super(stringBuilder.toString());
        }
    }

    public void add(int i) {
        synchronized (this.lock) {
            this.queue.add(Integer.valueOf(i));
            this.highestPriority = Math.max(this.highestPriority, i);
        }
    }

    public void proceed(int i) throws InterruptedException {
        synchronized (this.lock) {
            while (this.highestPriority != i) {
                this.lock.wait();
            }
        }
    }

    public boolean proceedNonBlocking(int i) {
        synchronized (this.lock) {
            i = this.highestPriority == i ? 1 : 0;
        }
        return i;
    }

    public void proceedOrThrow(int i) throws PriorityTooLowException {
        synchronized (this.lock) {
            if (this.highestPriority != i) {
                throw new PriorityTooLowException(i, this.highestPriority);
            }
        }
    }

    public void remove(int i) {
        synchronized (this.lock) {
            this.queue.remove(Integer.valueOf(i));
            this.highestPriority = this.queue.isEmpty() != 0 ? Integer.MIN_VALUE : ((Integer) this.queue.peek()).intValue();
            this.lock.notifyAll();
        }
    }
}
