package org.telegram.messenger;

import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda85 implements RequestDelegate {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC.InputMedia f$1;
    public final /* synthetic */ SendMessagesHelper.DelayedMessage f$2;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda85(SendMessagesHelper sendMessagesHelper, TLRPC.InputMedia inputMedia, SendMessagesHelper.DelayedMessage delayedMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = inputMedia;
        this.f$2 = delayedMessage;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m497xbfCLASSNAMEb8(this.f$1, this.f$2, tLObject, tL_error);
    }
}
