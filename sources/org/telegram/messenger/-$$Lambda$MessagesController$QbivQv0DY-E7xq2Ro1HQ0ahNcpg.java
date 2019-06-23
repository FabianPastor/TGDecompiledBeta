package org.telegram.messenger;

import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$QbivQv0DY-E7xq2Ro1HQ0ahNcpg implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ AlertDialog[] f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ BaseFragment f$3;

    public /* synthetic */ -$$Lambda$MessagesController$QbivQv0DY-E7xq2Ro1HQ0ahNcpg(MessagesController messagesController, AlertDialog[] alertDialogArr, int i, BaseFragment baseFragment) {
        this.f$0 = messagesController;
        this.f$1 = alertDialogArr;
        this.f$2 = i;
        this.f$3 = baseFragment;
    }

    public final void run() {
        this.f$0.lambda$openByUserName$264$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}
