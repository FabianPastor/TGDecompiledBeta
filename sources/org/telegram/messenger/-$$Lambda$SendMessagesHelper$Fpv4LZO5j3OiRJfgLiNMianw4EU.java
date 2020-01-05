package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$Fpv4LZO5j3OiRJfgLiNMianw4EU implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ boolean f$1;
    private final /* synthetic */ TL_error f$2;
    private final /* synthetic */ Message f$3;
    private final /* synthetic */ TLObject f$4;
    private final /* synthetic */ MessageObject f$5;
    private final /* synthetic */ String f$6;
    private final /* synthetic */ TLObject f$7;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$Fpv4LZO5j3OiRJfgLiNMianw4EU(SendMessagesHelper sendMessagesHelper, boolean z, TL_error tL_error, Message message, TLObject tLObject, MessageObject messageObject, String str, TLObject tLObject2) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = z;
        this.f$2 = tL_error;
        this.f$3 = message;
        this.f$4 = tLObject;
        this.f$5 = messageObject;
        this.f$6 = str;
        this.f$7 = tLObject2;
    }

    public final void run() {
        this.f$0.lambda$null$47$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
