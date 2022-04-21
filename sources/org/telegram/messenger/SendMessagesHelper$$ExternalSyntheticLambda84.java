package org.telegram.messenger;

import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda84 implements RequestDelegate {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ Object f$2;
    public final /* synthetic */ MessageObject f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ SendMessagesHelper.DelayedMessage f$5;
    public final /* synthetic */ boolean f$6;
    public final /* synthetic */ SendMessagesHelper.DelayedMessage f$7;
    public final /* synthetic */ boolean f$8;
    public final /* synthetic */ TLRPC.Message f$9;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda84(SendMessagesHelper sendMessagesHelper, TLObject tLObject, Object obj, MessageObject messageObject, String str, SendMessagesHelper.DelayedMessage delayedMessage, boolean z, SendMessagesHelper.DelayedMessage delayedMessage2, boolean z2, TLRPC.Message message) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tLObject;
        this.f$2 = obj;
        this.f$3 = messageObject;
        this.f$4 = str;
        this.f$5 = delayedMessage;
        this.f$6 = z;
        this.f$7 = delayedMessage2;
        this.f$8 = z2;
        this.f$9 = message;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m453xCLASSNAMEba465(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, tLObject, tL_error);
    }
}
