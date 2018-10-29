package org.telegram.messenger;

import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class MessagesController$$Lambda$145 implements Runnable {
    private final MessagesController arg$1;
    private final AlertDialog[] arg$2;
    private final int arg$3;
    private final BaseFragment arg$4;

    MessagesController$$Lambda$145(MessagesController messagesController, AlertDialog[] alertDialogArr, int i, BaseFragment baseFragment) {
        this.arg$1 = messagesController;
        this.arg$2 = alertDialogArr;
        this.arg$3 = i;
        this.arg$4 = baseFragment;
    }

    public void run() {
        this.arg$1.lambda$openByUserName$240$MessagesController(this.arg$2, this.arg$3, this.arg$4);
    }
}
