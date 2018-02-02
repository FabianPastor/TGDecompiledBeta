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
    private long positionMs = C.TIME_UNSET;
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

    public PlayerMessage(Sender sender, Target target, Timeline timeline, int defaultWindowIndex, Handler defaultHandler) {
        this.sender = sender;
        this.target = target;
        this.timeline = timeline;
        this.handler = defaultHandler;
        this.windowIndex = defaultWindowIndex;
    }

    public Timeline getTimeline() {
        return this.timeline;
    }

    public Target getTarget() {
        return this.target;
    }

    public PlayerMessage setType(int messageType) {
        Assertions.checkState(!this.isSent);
        this.type = messageType;
        return this;
    }

    public int getType() {
        return this.type;
    }

    public PlayerMessage setPayload(Object payload) {
        Assertions.checkState(!this.isSent);
        this.payload = payload;
        return this;
    }

    public Object getPayload() {
        return this.payload;
    }

    public PlayerMessage setHandler(Handler handler) {
        Assertions.checkState(!this.isSent);
        this.handler = handler;
        return this;
    }

    public Handler getHandler() {
        return this.handler;
    }

    public PlayerMessage setPosition(long positionMs) {
        Assertions.checkState(!this.isSent);
        this.positionMs = positionMs;
        return this;
    }

    public long getPositionMs() {
        return this.positionMs;
    }

    public PlayerMessage setPosition(int windowIndex, long positionMs) {
        boolean z;
        boolean z2 = true;
        if (this.isSent) {
            z = false;
        } else {
            z = true;
        }
        Assertions.checkState(z);
        if (positionMs == C.TIME_UNSET) {
            z2 = false;
        }
        Assertions.checkArgument(z2);
        if (windowIndex < 0 || (!this.timeline.isEmpty() && windowIndex >= this.timeline.getWindowCount())) {
            throw new IllegalSeekPositionException(this.timeline, windowIndex, positionMs);
        }
        this.windowIndex = windowIndex;
        this.positionMs = positionMs;
        return this;
    }

    public int getWindowIndex() {
        return this.windowIndex;
    }

    public PlayerMessage setDeleteAfterDelivery(boolean deleteAfterDelivery) {
        Assertions.checkState(!this.isSent);
        this.deleteAfterDelivery = deleteAfterDelivery;
        return this;
    }

    public boolean getDeleteAfterDelivery() {
        return this.deleteAfterDelivery;
    }

    public PlayerMessage send() {
        Assertions.checkState(!this.isSent);
        if (this.positionMs == C.TIME_UNSET) {
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

    public synchronized void markAsProcessed(boolean isDelivered) {
        this.isDelivered |= isDelivered;
        this.isProcessed = true;
        notifyAll();
    }
}
