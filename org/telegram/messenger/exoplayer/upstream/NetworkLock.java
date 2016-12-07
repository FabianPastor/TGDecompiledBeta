package org.telegram.messenger.exoplayer.upstream;

import java.io.IOException;
import java.util.PriorityQueue;
import org.telegram.tgnet.ConnectionsManager;

public final class NetworkLock {
    public static final int DOWNLOAD_PRIORITY = 10;
    public static final int STREAMING_PRIORITY = 0;
    public static final NetworkLock instance = new NetworkLock();
    private int highestPriority = ConnectionsManager.DEFAULT_DATACENTER_ID;
    private final Object lock = new Object();
    private final PriorityQueue<Integer> queue = new PriorityQueue();

    public static class PriorityTooLowException extends IOException {
        public PriorityTooLowException(int priority, int highestPriority) {
            super("Priority too low [priority=" + priority + ", highest=" + highestPriority + "]");
        }
    }

    private NetworkLock() {
    }

    public void proceed(int priority) throws InterruptedException {
        synchronized (this.lock) {
            while (this.highestPriority < priority) {
                this.lock.wait();
            }
        }
    }

    public boolean proceedNonBlocking(int priority) {
        boolean z;
        synchronized (this.lock) {
            z = this.highestPriority >= priority;
        }
        return z;
    }

    public void proceedOrThrow(int priority) throws PriorityTooLowException {
        synchronized (this.lock) {
            if (this.highestPriority < priority) {
                throw new PriorityTooLowException(priority, this.highestPriority);
            }
        }
    }

    public void add(int priority) {
        synchronized (this.lock) {
            this.queue.add(Integer.valueOf(priority));
            this.highestPriority = Math.min(this.highestPriority, priority);
        }
    }

    public void remove(int priority) {
        synchronized (this.lock) {
            this.queue.remove(Integer.valueOf(priority));
            this.highestPriority = this.queue.isEmpty() ? ConnectionsManager.DEFAULT_DATACENTER_ID : ((Integer) this.queue.peek()).intValue();
            this.lock.notifyAll();
        }
    }
}
