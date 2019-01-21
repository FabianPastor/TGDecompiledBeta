package org.telegram.ui.Adapters;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class MentionsAdapter$$Lambda$7 implements Runnable {
    private final MentionsAdapter arg$1;
    private final String arg$2;
    private final boolean arg$3;
    private final TLObject arg$4;
    private final User arg$5;
    private final String arg$6;
    private final MessagesStorage arg$7;
    private final String arg$8;

    MentionsAdapter$$Lambda$7(MentionsAdapter mentionsAdapter, String str, boolean z, TLObject tLObject, User user, String str2, MessagesStorage messagesStorage, String str3) {
        this.arg$1 = mentionsAdapter;
        this.arg$2 = str;
        this.arg$3 = z;
        this.arg$4 = tLObject;
        this.arg$5 = user;
        this.arg$6 = str2;
        this.arg$7 = messagesStorage;
        this.arg$8 = str3;
    }

    public void run() {
        this.arg$1.lambda$null$3$MentionsAdapter(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8);
    }
}
