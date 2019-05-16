package org.telegram.ui.Adapters;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MentionsAdapter$HVDPv79fAM1RehGsWWysAxv-W1Q implements Runnable {
    private final /* synthetic */ MentionsAdapter f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ TLObject f$3;
    private final /* synthetic */ User f$4;
    private final /* synthetic */ String f$5;
    private final /* synthetic */ MessagesStorage f$6;
    private final /* synthetic */ String f$7;

    public /* synthetic */ -$$Lambda$MentionsAdapter$HVDPv79fAM1RehGsWWysAxv-W1Q(MentionsAdapter mentionsAdapter, String str, boolean z, TLObject tLObject, User user, String str2, MessagesStorage messagesStorage, String str3) {
        this.f$0 = mentionsAdapter;
        this.f$1 = str;
        this.f$2 = z;
        this.f$3 = tLObject;
        this.f$4 = user;
        this.f$5 = str2;
        this.f$6 = messagesStorage;
        this.f$7 = str3;
    }

    public final void run() {
        this.f$0.lambda$null$3$MentionsAdapter(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
