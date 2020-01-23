package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_chatOnlines;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$x4Wp9NSNkBE8xfGIlkxGCWLGzgg implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ TL_chatOnlines f$2;

    public /* synthetic */ -$$Lambda$MessagesController$x4Wp9NSNkBE8xfGIlkxGCWLGzgg(MessagesController messagesController, int i, TL_chatOnlines tL_chatOnlines) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = tL_chatOnlines;
    }

    public final void run() {
        this.f$0.lambda$null$99$MessagesController(this.f$1, this.f$2);
    }
}
