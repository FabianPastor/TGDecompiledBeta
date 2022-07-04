package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda26 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ TLRPC.updates_Difference f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda26(MessagesController messagesController, ArrayList arrayList, TLRPC.updates_Difference updates_difference) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
        this.f$2 = updates_difference;
    }

    public final void run() {
        this.f$0.m224x6430fa5(this.f$1, this.f$2);
    }
}
