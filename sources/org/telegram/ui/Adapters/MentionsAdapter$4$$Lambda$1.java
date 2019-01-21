package org.telegram.ui.Adapters;

import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.Adapters.MentionsAdapter.AnonymousClass4;

final /* synthetic */ class MentionsAdapter$4$$Lambda$1 implements Runnable {
    private final AnonymousClass4 arg$1;
    private final String arg$2;
    private final TL_error arg$3;
    private final TLObject arg$4;
    private final MessagesController arg$5;
    private final MessagesStorage arg$6;

    MentionsAdapter$4$$Lambda$1(AnonymousClass4 anonymousClass4, String str, TL_error tL_error, TLObject tLObject, MessagesController messagesController, MessagesStorage messagesStorage) {
        this.arg$1 = anonymousClass4;
        this.arg$2 = str;
        this.arg$3 = tL_error;
        this.arg$4 = tLObject;
        this.arg$5 = messagesController;
        this.arg$6 = messagesStorage;
    }

    public void run() {
        this.arg$1.lambda$null$0$MentionsAdapter$4(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}
