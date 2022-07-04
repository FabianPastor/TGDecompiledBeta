package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda31 implements Runnable {
    public final /* synthetic */ DialogsActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ TLRPC.Chat f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ boolean f$5;

    public /* synthetic */ DialogsActivity$$ExternalSyntheticLambda31(DialogsActivity dialogsActivity, int i, long j, TLRPC.Chat chat, boolean z, boolean z2) {
        this.f$0 = dialogsActivity;
        this.f$1 = i;
        this.f$2 = j;
        this.f$3 = chat;
        this.f$4 = z;
        this.f$5 = z2;
    }

    public final void run() {
        this.f$0.m3408x64cf3var_(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
