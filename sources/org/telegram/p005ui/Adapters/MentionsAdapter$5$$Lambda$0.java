package org.telegram.p005ui.Adapters;

import org.telegram.messenger.MessagesController;
import org.telegram.p005ui.Adapters.MentionsAdapter.C04465;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.Adapters.MentionsAdapter$5$$Lambda$0 */
final /* synthetic */ class MentionsAdapter$5$$Lambda$0 implements RequestDelegate {
    private final C04465 arg$1;
    private final int arg$2;
    private final MessagesController arg$3;

    MentionsAdapter$5$$Lambda$0(C04465 c04465, int i, MessagesController messagesController) {
        this.arg$1 = c04465;
        this.arg$2 = i;
        this.arg$3 = messagesController;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$run$1$MentionsAdapter$5(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
