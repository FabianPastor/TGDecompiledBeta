package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.InputMedia;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$FZjjnxbzRKAZfXzHRe_dY-fA1KE implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ TLObject f$1;
    private final /* synthetic */ InputMedia f$2;
    private final /* synthetic */ DelayedMessage f$3;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$FZjjnxbzRKAZfXzHRe_dY-fA1KE(SendMessagesHelper sendMessagesHelper, TLObject tLObject, InputMedia inputMedia, DelayedMessage delayedMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tLObject;
        this.f$2 = inputMedia;
        this.f$3 = delayedMessage;
    }

    public final void run() {
        this.f$0.lambda$null$26$SendMessagesHelper(this.f$1, this.f$2, this.f$3);
    }
}