package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Chat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FileRefController$ezrB_EEVIghp6y7yWEa40dOLdLU implements Runnable {
    private final /* synthetic */ FileRefController f$0;
    private final /* synthetic */ Chat f$1;

    public /* synthetic */ -$$Lambda$FileRefController$ezrB_EEVIghp6y7yWEa40dOLdLU(FileRefController fileRefController, Chat chat) {
        this.f$0 = fileRefController;
        this.f$1 = chat;
    }

    public final void run() {
        this.f$0.lambda$onRequestComplete$22$FileRefController(this.f$1);
    }
}
