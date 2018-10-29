package org.telegram.messenger;

import org.telegram.tgnet.NativeByteBuffer;

final /* synthetic */ class MessagesStorage$$Lambda$5 implements Runnable {
    private final MessagesStorage arg$1;
    private final long arg$2;
    private final NativeByteBuffer arg$3;

    MessagesStorage$$Lambda$5(MessagesStorage messagesStorage, long j, NativeByteBuffer nativeByteBuffer) {
        this.arg$1 = messagesStorage;
        this.arg$2 = j;
        this.arg$3 = nativeByteBuffer;
    }

    public void run() {
        this.arg$1.lambda$createPendingTask$6$MessagesStorage(this.arg$2, this.arg$3);
    }
}
