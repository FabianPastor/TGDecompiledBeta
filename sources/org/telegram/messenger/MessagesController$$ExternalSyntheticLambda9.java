package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ TLRPC.updates_ChannelDifference f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda9(MessagesController messagesController, ArrayList arrayList, TLRPC.updates_ChannelDifference updates_channeldifference) {
        this.f$0 = messagesController;
        this.f$1 = arrayList;
        this.f$2 = updates_channeldifference;
    }

    public final void run() {
        this.f$0.m208xa3b80bb4(this.f$1, this.f$2);
    }
}
