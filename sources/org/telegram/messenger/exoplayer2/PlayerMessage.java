package org.telegram.messenger.exoplayer2;

import android.os.Handler;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class PlayerMessage {
    private boolean deleteAfterDelivery = true;
    private Handler handler;
    private boolean isDelivered;
    private boolean isProcessed;
    private boolean isSent;
    private Object payload;
    private long positionMs = 1;
    private final Sender sender;
    private final Target target;
    private final Timeline timeline;
    private int type;
    private int windowIndex;

    public interface Sender {
        void sendMessage(PlayerMessage playerMessage);
    }

    public interface Target {
        void handleMessage(int i, Object obj) throws ExoPlaybackException;
    }

    public PlayerMessage(Sender sender, Target target, Timeline timeline, int i, Handler handler) {
        this.sender = sender;
        this.target = target;
        this.timeline = timeline;
        this.handler = handler;
        this.windowIndex = i;
    }

    public Timeline getTimeline() {
        return this.timeline;
    }

    public Target getTarget() {
        return this.target;
    }

    public PlayerMessage setType(int i) {
        Assertions.checkState(this.isSent ^ 1);
        this.type = i;
        return this;
    }

    public int getType() {
        return this.type;
    }

    public PlayerMessage setPayload(Object obj) {
        Assertions.checkState(this.isSent ^ 1);
        this.payload = obj;
        return this;
    }

    public Object getPayload() {
        return this.payload;
    }

    public PlayerMessage setHandler(Handler handler) {
        Assertions.checkState(this.isSent ^ 1);
        this.handler = handler;
        return this;
    }

    public Handler getHandler() {
        return this.handler;
    }

    public PlayerMessage setPosition(long j) {
        Assertions.checkState(this.isSent ^ 1);
        this.positionMs = j;
        return this;
    }

    public long getPositionMs() {
        return this.positionMs;
    }

    public PlayerMessage setPosition(int i, long j) {
        boolean z = true;
        Assertions.checkState(this.isSent ^ true);
        if (j == C0542C.TIME_UNSET) {
            z = false;
        }
        Assertions.checkArgument(z);
        if (i >= 0) {
            if (this.timeline.isEmpty() || i < this.timeline.getWindowCount()) {
                this.windowIndex = i;
                this.positionMs = j;
                return this;
            }
        }
        throw new IllegalSeekPositionException(this.timeline, i, j);
    }

    public int getWindowIndex() {
        return this.windowIndex;
    }

    public PlayerMessage setDeleteAfterDelivery(boolean z) {
        Assertions.checkState(this.isSent ^ 1);
        this.deleteAfterDelivery = z;
        return this;
    }

    public boolean getDeleteAfterDelivery() {
        return this.deleteAfterDelivery;
    }

    public PlayerMessage send() {
        Assertions.checkState(this.isSent ^ true);
        if (this.positionMs == C0542C.TIME_UNSET) {
            Assertions.checkArgument(this.deleteAfterDelivery);
        }
        this.isSent = true;
        this.sender.sendMessage(this);
        return this;
    }

    public synchronized boolean blockUntilDelivered() throws InterruptedException {
        Assertions.checkState(this.isSent);
        Assertions.checkState(this.handler.getLooper().getThread() != Thread.currentThread());
        while (!this.isProcessed) {
            wait();
        }
        return this.isDelivered;
    }

    public synchronized void markAsProcessed(boolean z) {
        this.isDelivered = z | this.isDelivered;
        this.isProcessed = true;
        notifyAll();
    }
}
