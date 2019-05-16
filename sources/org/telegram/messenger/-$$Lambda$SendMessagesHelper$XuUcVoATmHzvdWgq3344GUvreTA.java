package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SendMessagesHelper$XuUcVoATmHzvdWgq3344GUvreTA implements Runnable {
    private final /* synthetic */ SendMessagesHelper f$0;
    private final /* synthetic */ TL_updateNewChannelMessage f$1;

    public /* synthetic */ -$$Lambda$SendMessagesHelper$XuUcVoATmHzvdWgq3344GUvreTA(SendMessagesHelper sendMessagesHelper, TL_updateNewChannelMessage tL_updateNewChannelMessage) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = tL_updateNewChannelMessage;
    }

    public final void run() {
        this.f$0.lambda$null$35$SendMessagesHelper(this.f$1);
    }
}
