package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$9ODWo0AcLKN4v8iMKaGws9a7WcI implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ MessageObject f$1;
    private final /* synthetic */ Message f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ boolean f$4;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$9ODWo0AcLKN4v8iMKaGws9a7WcI(SendMessagesHelper sendMessagesHelper, MessageObject messageObject, Message message, int i, boolean z) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = messageObject;
        this.f$2 = message;
        this.f$3 = i;
        this.f$4 = z;
    }

    public final void run() {
        this.f$0.lambda$null$43$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
