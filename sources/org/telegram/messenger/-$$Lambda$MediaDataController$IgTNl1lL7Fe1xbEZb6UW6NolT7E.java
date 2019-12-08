package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Message;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$IgTNl1lL7Fe1xbEZb6UW6NolT7E implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ long f$1;
    private final /* synthetic */ Message f$2;

    public /* synthetic */ -$$Lambda$MediaDataController$IgTNl1lL7Fe1xbEZb6UW6NolT7E(MediaDataController mediaDataController, long j, Message message) {
        this.f$0 = mediaDataController;
        this.f$1 = j;
        this.f$2 = message;
    }

    public final void run() {
        this.f$0.lambda$putBotKeyboard$111$MediaDataController(this.f$1, this.f$2);
    }
}
