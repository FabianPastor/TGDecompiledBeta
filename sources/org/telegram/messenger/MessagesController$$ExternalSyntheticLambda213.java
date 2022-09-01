package org.telegram.messenger;

import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda213 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ AlertDialog[] f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ BaseFragment f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda213(MessagesController messagesController, AlertDialog[] alertDialogArr, int i, BaseFragment baseFragment) {
        this.f$0 = messagesController;
        this.f$1 = alertDialogArr;
        this.f$2 = i;
        this.f$3 = baseFragment;
    }

    public final void run() {
        this.f$0.lambda$openByUserName$351(this.f$1, this.f$2, this.f$3);
    }
}
