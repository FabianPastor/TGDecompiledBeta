package org.telegram.ui;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class DialogsActivity$$ExternalSyntheticLambda36 implements Runnable {
    public final /* synthetic */ DialogsActivity f$0;
    public final /* synthetic */ MessagesController.DialogFilter f$1;
    public final /* synthetic */ TLRPC.Dialog f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ DialogsActivity$$ExternalSyntheticLambda36(DialogsActivity dialogsActivity, MessagesController.DialogFilter dialogFilter, TLRPC.Dialog dialog, long j) {
        this.f$0 = dialogsActivity;
        this.f$1 = dialogFilter;
        this.f$2 = dialog;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.m3413lambda$showChatPreview$26$orgtelegramuiDialogsActivity(this.f$1, this.f$2, this.f$3);
    }
}
