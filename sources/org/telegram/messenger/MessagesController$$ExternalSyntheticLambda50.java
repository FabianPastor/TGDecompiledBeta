package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$messages_Messages;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda50 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC$messages_Messages f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ int f$5;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda50(MessagesController messagesController, int i, TLRPC$messages_Messages tLRPC$messages_Messages, boolean z, boolean z2, int i2) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = tLRPC$messages_Messages;
        this.f$3 = z;
        this.f$4 = z2;
        this.f$5 = i2;
    }

    public final void run() {
        this.f$0.lambda$processLoadedMessages$151(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
