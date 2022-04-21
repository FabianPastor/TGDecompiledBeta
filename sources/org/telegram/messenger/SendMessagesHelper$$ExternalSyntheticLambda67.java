package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda67 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;
    public final /* synthetic */ TLRPC.Message f$3;
    public final /* synthetic */ TLObject f$4;
    public final /* synthetic */ MessageObject f$5;
    public final /* synthetic */ String f$6;
    public final /* synthetic */ TLObject f$7;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda67(SendMessagesHelper sendMessagesHelper, boolean z, TLRPC.TL_error tL_error, TLRPC.Message message, TLObject tLObject, MessageObject messageObject, String str, TLObject tLObject2) {
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
        this.f$0.m452x10dc1fcf(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
