package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda124 implements MessagesStorage.IntCallback {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ TLRPC.InputPeer f$4;
    public final /* synthetic */ long f$5;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda124(MessagesController messagesController, long j, int i, boolean z, TLRPC.InputPeer inputPeer, long j2) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = i;
        this.f$3 = z;
        this.f$4 = inputPeer;
        this.f$5 = j2;
    }

    public final void run(int i) {
        this.f$0.m166x20674eff(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, i);
    }
}
