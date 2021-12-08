package org.telegram.ui.Adapters;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MentionsAdapter$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ MentionsAdapter f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ TLObject f$3;
    public final /* synthetic */ TLRPC.User f$4;
    public final /* synthetic */ String f$5;
    public final /* synthetic */ MessagesStorage f$6;
    public final /* synthetic */ String f$7;

    public /* synthetic */ MentionsAdapter$$ExternalSyntheticLambda4(MentionsAdapter mentionsAdapter, String str, boolean z, TLObject tLObject, TLRPC.User user, String str2, MessagesStorage messagesStorage, String str3) {
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
        this.f$0.m1377xCLASSNAMEa7e4c(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
    }
}
