package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$N8IQQbM3W17ywXUW7wSpWRXSjrI implements RequestDelegate {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ TLObject f$1;
    private final /* synthetic */ Object f$2;
    private final /* synthetic */ MessageObject f$3;
    private final /* synthetic */ String f$4;
    private final /* synthetic */ DelayedMessage f$5;
    private final /* synthetic */ boolean f$6;
    private final /* synthetic */ DelayedMessage f$7;
    private final /* synthetic */ Message f$8;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$N8IQQbM3W17ywXUW7wSpWRXSjrI(SendMessagesHelper sendMessagesHelper, TLObject tLObject, Object obj, MessageObject messageObject, String str, DelayedMessage delayedMessage, boolean z, DelayedMessage delayedMessage2, Message message) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tLObject;
        this.f$2 = obj;
        this.f$3 = messageObject;
        this.f$4 = str;
        this.f$5 = delayedMessage;
        this.f$6 = z;
        this.f$7 = delayedMessage2;
        this.f$8 = message;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$performSendMessageRequest$41$SendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, tLObject, tL_error);
    }
}
