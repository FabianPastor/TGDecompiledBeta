package org.telegram.messenger;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda125 implements MessagesStorage.IntCallback {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.Dialog f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda125(MessagesController messagesController, TLRPC.Dialog dialog, int i, long j) {
        this.f$0 = messagesController;
        this.f$1 = dialog;
        this.f$2 = i;
        this.f$3 = j;
    }

    public final void run(int i) {
        this.f$0.m408xCLASSNAMEd4(this.f$1, this.f$2, this.f$3, i);
    }
}
