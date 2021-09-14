package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLRPC$InputPeer;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda204 implements MessagesStorage.IntCallback {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ TLRPC$InputPeer f$4;
    public final /* synthetic */ long f$5;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda204(MessagesController messagesController, long j, int i, boolean z, TLRPC$InputPeer tLRPC$InputPeer, long j2) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = z;
        this.f$4 = tLRPC$InputPeer;
        this.f$5 = j2;
    }

    public final void run(int i) {
        this.f$0.lambda$deleteDialog$101(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, i);
    }
}
