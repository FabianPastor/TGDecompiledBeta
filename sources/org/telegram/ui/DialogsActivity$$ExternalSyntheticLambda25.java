package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda25 implements Runnable {
    public final /* synthetic */ DialogsActivity f$0;
    public final /* synthetic */ TLRPC.Chat f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ TLRPC.User f$4;

    public /* synthetic */ DialogsActivity$$ExternalSyntheticLambda25(DialogsActivity dialogsActivity, TLRPC.Chat chat, long j, boolean z, TLRPC.User user) {
        this.f$0 = dialogsActivity;
        this.f$1 = chat;
        this.f$2 = j;
        this.f$3 = z;
        this.f$4 = user;
    }

    public final void run() {
        this.f$0.m2846xe53var_(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
