package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda91 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.updates_ChannelDifference f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda91(MessagesController messagesController, TLRPC.updates_ChannelDifference updates_channeldifference) {
        this.f$0 = messagesController;
        this.f$1 = updates_channeldifference;
    }

    public final void run() {
        this.f$0.m204x40543adb(this.f$1);
    }
}
