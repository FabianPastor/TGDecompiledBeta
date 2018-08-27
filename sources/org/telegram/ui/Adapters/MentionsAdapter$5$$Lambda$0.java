package org.telegram.ui.Adapters;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.Adapters.MentionsAdapter.C05105;

final /* synthetic */ class MentionsAdapter$5$$Lambda$0 implements RequestDelegate {
    private final C05105 arg$1;
    private final int arg$2;
    private final MessagesController arg$3;

    MentionsAdapter$5$$Lambda$0(C05105 c05105, int i, MessagesController messagesController) {
        this.arg$1 = c05105;
        this.arg$2 = i;
        this.arg$3 = messagesController;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$run$1$MentionsAdapter$5(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
