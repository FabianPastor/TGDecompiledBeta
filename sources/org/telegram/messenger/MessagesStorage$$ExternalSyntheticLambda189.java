package org.telegram.messenger;

import org.telegram.tgnet.NativeByteBuffer;

public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda189 implements Runnable {
    public final /* synthetic */ MessagesStorage f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ NativeByteBuffer f$2;

    public /* synthetic */ MessagesStorage$$ExternalSyntheticLambda189(MessagesStorage messagesStorage, long j, NativeByteBuffer nativeByteBuffer) {
        this.f$0 = messagesStorage;
        this.f$1 = j;
        this.f$2 = nativeByteBuffer;
    }

    public final void run() {
        this.f$0.m925x9e425e5f(this.f$1, this.f$2);
    }
}
