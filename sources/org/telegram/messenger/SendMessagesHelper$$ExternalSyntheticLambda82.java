package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda82 implements RequestDelegate {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ MessageObject f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ Runnable f$3;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda82(SendMessagesHelper sendMessagesHelper, MessageObject messageObject, String str, Runnable runnable) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = messageObject;
        this.f$2 = str;
        this.f$3 = runnable;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m493lambda$sendVote$21$orgtelegrammessengerSendMessagesHelper(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
