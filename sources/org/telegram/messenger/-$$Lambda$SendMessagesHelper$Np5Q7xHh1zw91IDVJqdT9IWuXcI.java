package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_forwardMessages;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$Np5Q7xHh1zw91IDVJqdT9IWuXcI implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TL_messages_forwardMessages f$2;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$Np5Q7xHh1zw91IDVJqdT9IWuXcI(SendMessagesHelper sendMessagesHelper, TL_error tL_error, TL_messages_forwardMessages tL_messages_forwardMessages) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tL_error;
        this.f$2 = tL_messages_forwardMessages;
    }

    public final void run() {
        this.f$0.lambda$null$12$SendMessagesHelper(this.f$1, this.f$2);
    }
}