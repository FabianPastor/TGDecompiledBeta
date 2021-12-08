package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda46 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ AlertDialog[] f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda46(ChatActivity chatActivity, AlertDialog[] alertDialogArr, TLObject tLObject, String str) {
        this.f$0 = chatActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = tLObject;
        this.f$3 = str;
    }

    public final void run() {
        this.f$0.m1722lambda$didPressMessageUrl$164$orgtelegramuiChatActivity(this.f$1, this.f$2, this.f$3);
    }
}
