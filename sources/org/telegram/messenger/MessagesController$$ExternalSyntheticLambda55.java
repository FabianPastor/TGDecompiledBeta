package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_chatOnlines;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda55 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC$TL_chatOnlines f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda55(MessagesController messagesController, int i, TLRPC$TL_chatOnlines tLRPC$TL_chatOnlines) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = tLRPC$TL_chatOnlines;
    }

    public final void run() {
        this.f$0.lambda$updateTimerProc$119(this.f$1, this.f$2);
    }
}
