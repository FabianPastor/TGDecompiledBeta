package org.telegram.ui.Adapters;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class MentionsAdapter$$ExternalSyntheticLambda8 implements RequestDelegate {
    public final /* synthetic */ MentionsAdapter f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ TLRPC$User f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ MessagesStorage f$5;
    public final /* synthetic */ String f$6;

    public /* synthetic */ MentionsAdapter$$ExternalSyntheticLambda8(MentionsAdapter mentionsAdapter, String str, boolean z, TLRPC$User tLRPC$User, String str2, MessagesStorage messagesStorage, String str3) {
        this.f$0 = mentionsAdapter;
        this.f$1 = str;
        this.f$2 = z;
        this.f$3 = tLRPC$User;
        this.f$4 = str2;
        this.f$5 = messagesStorage;
        this.f$6 = str3;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$searchForContextBotResults$6(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tLRPC$TL_error);
    }
}
