package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda182 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$UserFull f$1;
    public final /* synthetic */ TLRPC$User f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda182(MessagesController messagesController, TLRPC$UserFull tLRPC$UserFull, TLRPC$User tLRPC$User, int i) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$UserFull;
        this.f$2 = tLRPC$User;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$loadFullUser$48(this.f$1, this.f$2, this.f$3);
    }
}
