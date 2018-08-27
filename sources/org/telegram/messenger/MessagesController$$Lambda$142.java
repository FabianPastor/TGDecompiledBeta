package org.telegram.messenger;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class MessagesController$$Lambda$142 implements RequestDelegate {
    private final MessagesController arg$1;
    private final AlertDialog arg$2;
    private final BaseFragment arg$3;
    private final Bundle arg$4;

    MessagesController$$Lambda$142(MessagesController messagesController, AlertDialog alertDialog, BaseFragment baseFragment, Bundle bundle) {
        this.arg$1 = messagesController;
        this.arg$2 = alertDialog;
        this.arg$3 = baseFragment;
        this.arg$4 = bundle;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$checkCanOpenChat$235$MessagesController(this.arg$2, this.arg$3, this.arg$4, tLObject, tL_error);
    }
}
