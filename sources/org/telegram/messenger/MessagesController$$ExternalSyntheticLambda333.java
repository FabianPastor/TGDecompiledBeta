package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda333 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ TLRPC.TL_channels_channelParticipant f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda333(MessagesController messagesController, long j, ArrayList arrayList, TLRPC.TL_channels_channelParticipant tL_channels_channelParticipant) {
        this.f$0 = messagesController;
        this.f$1 = j;
        this.f$2 = arrayList;
        this.f$3 = tL_channels_channelParticipant;
    }

    public final void run() {
        this.f$0.m133x9var_c6ae(this.f$1, this.f$2, this.f$3);
    }
}
