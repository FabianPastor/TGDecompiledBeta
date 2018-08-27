package org.telegram.messenger;

import android.os.Bundle;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class MessagesController$$Lambda$148 implements Runnable {
    private final MessagesController arg$1;
    private final AlertDialog arg$2;
    private final TLObject arg$3;
    private final BaseFragment arg$4;
    private final Bundle arg$5;

    MessagesController$$Lambda$148(MessagesController messagesController, AlertDialog alertDialog, TLObject tLObject, BaseFragment baseFragment, Bundle bundle) {
        this.arg$1 = messagesController;
        this.arg$2 = alertDialog;
        this.arg$3 = tLObject;
        this.arg$4 = baseFragment;
        this.arg$5 = bundle;
    }

    public void run() {
        this.arg$1.lambda$null$234$MessagesController(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
