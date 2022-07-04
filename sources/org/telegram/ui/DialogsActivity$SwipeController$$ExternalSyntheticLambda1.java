package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.DialogsActivity;

public final /* synthetic */ class DialogsActivity$SwipeController$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ DialogsActivity.SwipeController f$0;
    public final /* synthetic */ TLRPC.Dialog f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ DialogsActivity$SwipeController$$ExternalSyntheticLambda1(DialogsActivity.SwipeController swipeController, TLRPC.Dialog dialog, int i) {
        this.f$0 = swipeController;
        this.f$1 = dialog;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.m3436x3fb9d364(this.f$1, this.f$2);
    }
}
