package org.telegram.ui;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ChatActivity.AnonymousClass16;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$16$BC8t44pPzeiGUytAIKRsBXYujF0 implements Runnable {
    private final /* synthetic */ AnonymousClass16 f$0;
    private final /* synthetic */ TLObject f$1;
    private final /* synthetic */ TL_error f$2;
    private final /* synthetic */ MessagesStorage f$3;

    public /* synthetic */ -$$Lambda$ChatActivity$16$BC8t44pPzeiGUytAIKRsBXYujF0(AnonymousClass16 anonymousClass16, TLObject tLObject, TL_error tL_error, MessagesStorage messagesStorage) {
        this.f$0 = anonymousClass16;
        this.f$1 = tLObject;
        this.f$2 = tL_error;
        this.f$3 = messagesStorage;
    }

    public final void run() {
        this.f$0.lambda$null$1$ChatActivity$16(this.f$1, this.f$2, this.f$3);
    }
}
