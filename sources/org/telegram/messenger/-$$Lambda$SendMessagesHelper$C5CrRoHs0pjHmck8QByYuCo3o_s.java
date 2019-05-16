package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$C5CrRoHs0pjHmck8QByYuCo3o_s implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ Message f$2;
    private final /* synthetic */ TLObject f$3;
    private final /* synthetic */ MessageObject f$4;
    private final /* synthetic */ String f$5;
    private final /* synthetic */ TLObject f$6;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$C5CrRoHs0pjHmck8QByYuCo3o_s(SendMessagesHelper sendMessagesHelper, TL_error tL_error, Message message, TLObject tLObject, MessageObject messageObject, String str, TLObject tLObject2) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tL_error;
        this.f$2 = message;
        this.f$3 = tLObject;
        this.f$4 = messageObject;
        this.f$5 = str;
        this.f$6 = tLObject2;
    }

    public final void run() {
        this.f$0.lambda$null$32$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
