package org.telegram.ui.Adapters;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MentionsAdapter$5habXq1r64920QdCkG9j6xMgM4U implements RequestDelegate {
    private final /* synthetic */ MentionsAdapter f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ User f$3;
    private final /* synthetic */ String f$4;
    private final /* synthetic */ MessagesStorage f$5;
    private final /* synthetic */ String f$6;

    public /* synthetic */ -$$Lambda$MentionsAdapter$5habXq1r64920QdCkG9j6xMgM4U(MentionsAdapter mentionsAdapter, String str, boolean z, User user, String str2, MessagesStorage messagesStorage, String str3) {
        this.f$0 = mentionsAdapter;
        this.f$1 = str;
        this.f$2 = z;
        this.f$3 = user;
        this.f$4 = str2;
        this.f$5 = messagesStorage;
        this.f$6 = str3;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$searchForContextBotResults$4$MentionsAdapter(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tL_error);
    }
}
