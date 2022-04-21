package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda56 implements Runnable {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ TLRPC.TL_messages_sendMultiMedia f$1;
    public final /* synthetic */ SendMessagesHelper.DelayedMessage f$2;
    public final /* synthetic */ ArrayList f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda56(SendMessagesHelper sendMessagesHelper, TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia, SendMessagesHelper.DelayedMessage delayedMessage, ArrayList arrayList, boolean z) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tL_messages_sendMultiMedia;
        this.f$2 = delayedMessage;
        this.f$3 = arrayList;
        this.f$4 = z;
    }

    public final void run() {
        this.f$0.m456xb30048c0(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
