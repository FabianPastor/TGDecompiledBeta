package org.telegram.p005ui.Adapters;

import org.telegram.messenger.MessagesController;
import org.telegram.p005ui.Adapters.MentionsAdapter.C06895;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.Adapters.MentionsAdapter$5$$Lambda$1 */
final /* synthetic */ class MentionsAdapter$5$$Lambda$1 implements Runnable {
    private final C06895 arg$1;
    private final int arg$2;
    private final TL_error arg$3;
    private final TLObject arg$4;
    private final MessagesController arg$5;

    MentionsAdapter$5$$Lambda$1(C06895 c06895, int i, TL_error tL_error, TLObject tLObject, MessagesController messagesController) {
        this.arg$1 = c06895;
        this.arg$2 = i;
        this.arg$3 = tL_error;
        this.arg$4 = tLObject;
        this.arg$5 = messagesController;
    }

    public void run() {
        this.arg$1.lambda$null$0$MentionsAdapter$5(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
