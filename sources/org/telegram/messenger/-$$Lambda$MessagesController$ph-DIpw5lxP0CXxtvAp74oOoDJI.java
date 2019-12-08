package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$ph-DIpw5lxP0CXxtvAp74oOoDJI implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ AlertDialog[] f$1;
    private final /* synthetic */ BaseFragment f$2;
    private final /* synthetic */ TL_error f$3;
    private final /* synthetic */ TLObject f$4;
    private final /* synthetic */ int f$5;

    public /* synthetic */ -$$Lambda$MessagesController$ph-DIpw5lxP0CXxtvAp74oOoDJI(MessagesController messagesController, AlertDialog[] alertDialogArr, BaseFragment baseFragment, TL_error tL_error, TLObject tLObject, int i) {
        this.f$0 = messagesController;
        this.f$1 = alertDialogArr;
        this.f$2 = baseFragment;
        this.f$3 = tL_error;
        this.f$4 = tLObject;
        this.f$5 = i;
    }

    public final void run() {
        this.f$0.lambda$null$276$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
