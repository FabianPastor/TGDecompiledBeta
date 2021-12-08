package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.DialogsActivity;

public final /* synthetic */ class DialogsActivity$SwipeController$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ DialogsActivity.SwipeController f$0;
    public final /* synthetic */ TLRPC.Dialog f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ DialogsActivity$SwipeController$$ExternalSyntheticLambda2(DialogsActivity.SwipeController swipeController, TLRPC.Dialog dialog, int i, int i2) {
        this.f$0 = swipeController;
        this.f$1 = dialog;
        this.f$2 = i;
        this.f$3 = i2;
    }

    public final void run() {
        this.f$0.m2888x3var_d65(this.f$1, this.f$2, this.f$3);
    }
}
