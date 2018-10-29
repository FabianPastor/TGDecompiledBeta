package org.telegram.ui.Adapters;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class MentionsAdapter$$Lambda$3 implements RequestDelegate {
    private final MentionsAdapter arg$1;
    private final String arg$2;
    private final boolean arg$3;
    private final User arg$4;
    private final String arg$5;
    private final MessagesStorage arg$6;
    private final String arg$7;

    MentionsAdapter$$Lambda$3(MentionsAdapter mentionsAdapter, String str, boolean z, User user, String str2, MessagesStorage messagesStorage, String str3) {
        this.arg$1 = mentionsAdapter;
        this.arg$2 = str;
        this.arg$3 = z;
        this.arg$4 = user;
        this.arg$5 = str2;
        this.arg$6 = messagesStorage;
        this.arg$7 = str3;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$searchForContextBotResults$4$MentionsAdapter(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, tLObject, tL_error);
    }
}
