package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class MessagesController$$Lambda$147 implements Runnable {
    private final MessagesController arg$1;
    private final AlertDialog[] arg$2;
    private final BaseFragment arg$3;
    private final TL_error arg$4;
    private final TLObject arg$5;
    private final int arg$6;

    MessagesController$$Lambda$147(MessagesController messagesController, AlertDialog[] alertDialogArr, BaseFragment baseFragment, TL_error tL_error, TLObject tLObject, int i) {
        this.arg$1 = messagesController;
        this.arg$2 = alertDialogArr;
        this.arg$3 = baseFragment;
        this.arg$4 = tL_error;
        this.arg$5 = tLObject;
        this.arg$6 = i;
    }

    public void run() {
        this.arg$1.lambda$null$237$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6);
    }
}
