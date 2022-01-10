package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLRPC$Dialog;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda213 implements MessagesStorage.IntCallback {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$Dialog f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda213(MessagesController messagesController, TLRPC$Dialog tLRPC$Dialog, int i, long j) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$Dialog;
        this.f$2 = i;
        this.f$3 = j;
    }

    public final void run(int i) {
        this.f$0.lambda$updateInterfaceWithMessages$322(this.f$1, this.f$2, this.f$3, i);
    }
}
