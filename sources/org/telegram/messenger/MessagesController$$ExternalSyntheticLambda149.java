package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_channels_channelParticipant;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda149 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_channels_channelParticipant f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda149(MessagesController messagesController, TLRPC$TL_channels_channelParticipant tLRPC$TL_channels_channelParticipant) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_channels_channelParticipant;
    }

    public final void run() {
        this.f$0.lambda$checkChatInviter$287(this.f$1);
    }
}
