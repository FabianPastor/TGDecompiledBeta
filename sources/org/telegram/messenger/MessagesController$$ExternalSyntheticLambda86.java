package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$TL_channels_channelParticipant;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda86 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ TLRPC$TL_channels_channelParticipant f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda86(MessagesController messagesController, long j, ArrayList arrayList, TLRPC$TL_channels_channelParticipant tLRPC$TL_channels_channelParticipant) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = arrayList;
        this.f$3 = tLRPC$TL_channels_channelParticipant;
    }

    public final void run() {
        this.f$0.lambda$checkChatInviter$301(this.f$1, this.f$2, this.f$3);
    }
}
