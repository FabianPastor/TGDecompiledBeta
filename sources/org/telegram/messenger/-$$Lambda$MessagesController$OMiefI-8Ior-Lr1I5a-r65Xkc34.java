package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.updates_ChannelDifference;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$OMiefI-8Ior-Lr1I5a-r65XkCLASSNAME implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ updates_ChannelDifference f$1;

    public /* synthetic */ -$$Lambda$MessagesController$OMiefI-8Ior-Lr1I5a-r65XkCLASSNAME(MessagesController messagesController, updates_ChannelDifference updates_channeldifference) {
        this.f$0 = messagesController;
        this.f$1 = updates_channeldifference;
    }

    public final void run() {
        this.f$0.lambda$null$199$MessagesController(this.f$1);
    }
}
