package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda63 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.TL_channels_channelParticipant f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda63(MessagesController messagesController, TLRPC.TL_channels_channelParticipant tL_channels_channelParticipant) {
        this.f$0 = messagesController;
        this.f$1 = tL_channels_channelParticipant;
    }

    public final void run() {
        this.f$0.m133x1947var_(this.f$1);
    }
}
