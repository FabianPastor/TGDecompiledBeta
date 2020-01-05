package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_updateLangPack;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$TGaV3j0rMT2kXtICENm7gz6gqZw implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_updateLangPack f$1;

    public /* synthetic */ -$$Lambda$MessagesController$TGaV3j0rMT2kXtICENm7gz6gqZw(MessagesController messagesController, TL_updateLangPack tL_updateLangPack) {
        this.f$0 = messagesController;
        this.f$1 = tL_updateLangPack;
    }

    public final void run() {
        this.f$0.lambda$processUpdateArray$261$MessagesController(this.f$1);
    }
}
