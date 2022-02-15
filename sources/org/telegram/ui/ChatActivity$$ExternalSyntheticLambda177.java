package org.telegram.ui;

import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda177 implements Runnable {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ AlertDialog[] f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda177(ChatActivity chatActivity, AlertDialog[] alertDialogArr, int i) {
        this.f$0 = chatActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$showRequestUrlAlert$208(this.f$1, this.f$2);
    }
}
