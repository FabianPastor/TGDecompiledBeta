package org.telegram.p005ui.Adapters;

import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.p005ui.Adapters.MentionsAdapter.CLASSNAME;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.Adapters.MentionsAdapter$4$$Lambda$0 */
final /* synthetic */ class MentionsAdapter$4$$Lambda$0 implements RequestDelegate {
    private final CLASSNAME arg$1;
    private final String arg$2;
    private final MessagesController arg$3;
    private final MessagesStorage arg$4;

    MentionsAdapter$4$$Lambda$0(CLASSNAME CLASSNAME, String str, MessagesController messagesController, MessagesStorage messagesStorage) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = str;
        this.arg$3 = messagesController;
        this.arg$4 = messagesStorage;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$run$1$MentionsAdapter$4(this.arg$2, this.arg$3, this.arg$4, tLObject, tL_error);
    }
}
