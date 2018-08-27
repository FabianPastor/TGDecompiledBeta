package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_channels_channelParticipant;

final /* synthetic */ class MessagesController$$Lambda$157 implements Runnable {
    private final MessagesController arg$1;
    private final TL_channels_channelParticipant arg$2;

    MessagesController$$Lambda$157(MessagesController messagesController, TL_channels_channelParticipant tL_channels_channelParticipant) {
        this.arg$1 = messagesController;
        this.arg$2 = tL_channels_channelParticipant;
    }

    public void run() {
        this.arg$1.lambda$null$207$MessagesController(this.arg$2);
    }
}
