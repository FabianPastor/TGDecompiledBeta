package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda120 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ AlertDialog[] f$1;
    public final /* synthetic */ BaseFragment f$2;
    public final /* synthetic */ TLRPC.TL_error f$3;
    public final /* synthetic */ TLObject f$4;
    public final /* synthetic */ int f$5;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda120(MessagesController messagesController, AlertDialog[] alertDialogArr, BaseFragment baseFragment, TLRPC.TL_error tL_error, TLObject tLObject, int i) {
        this.f$0 = messagesController;
        this.f$1 = alertDialogArr;
        this.f$2 = baseFragment;
        this.f$3 = tL_error;
        this.f$4 = tLObject;
        this.f$5 = i;
    }

    public final void run() {
        this.f$0.m303x852dCLASSNAMEa(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
