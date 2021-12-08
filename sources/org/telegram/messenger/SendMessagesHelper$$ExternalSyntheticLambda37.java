package org.telegram.messenger;

import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda37 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC.Document f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ MessageObject f$3;
    public final /* synthetic */ MessageObject f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ Object f$7;
    public final /* synthetic */ MessageObject.SendAnimationData f$8;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda37(SendMessagesHelper sendMessagesHelper, TLRPC.Document document, long j, MessageObject messageObject, MessageObject messageObject2, boolean z, int i, Object obj, MessageObject.SendAnimationData sendAnimationData) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = document;
        this.f$2 = j;
        this.f$3 = messageObject;
        this.f$4 = messageObject2;
        this.f$5 = z;
        this.f$6 = i;
        this.f$7 = obj;
        this.f$8 = sendAnimationData;
    }

    public final void run() {
        this.f$0.m480lambda$sendSticker$6$orgtelegrammessengerSendMessagesHelper(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
