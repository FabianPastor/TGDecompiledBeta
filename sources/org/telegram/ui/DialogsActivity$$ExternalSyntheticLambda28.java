package org.telegram.ui;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda28 implements MessagesStorage.BooleanCallback {
    public final /* synthetic */ DialogsActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC.Chat f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ boolean f$4;

    public /* synthetic */ DialogsActivity$$ExternalSyntheticLambda28(DialogsActivity dialogsActivity, int i, TLRPC.Chat chat, long j, boolean z) {
        this.f$0 = dialogsActivity;
        this.f$1 = i;
        this.f$2 = chat;
        this.f$3 = j;
        this.f$4 = z;
    }

    public final void run(boolean z) {
        this.f$0.m2867xa5873728(this.f$1, this.f$2, this.f$3, this.f$4, z);
    }
}
