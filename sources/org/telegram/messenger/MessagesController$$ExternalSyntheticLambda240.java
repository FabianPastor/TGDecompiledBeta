package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda240 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ AlertDialog[] f$1;
    public final /* synthetic */ BaseFragment f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda240(MessagesController messagesController, AlertDialog[] alertDialogArr, BaseFragment baseFragment, int i) {
        this.f$0 = messagesController;
        this.f$1 = alertDialogArr;
        this.f$2 = baseFragment;
        this.f$3 = i;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m296x127d0fc8(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
