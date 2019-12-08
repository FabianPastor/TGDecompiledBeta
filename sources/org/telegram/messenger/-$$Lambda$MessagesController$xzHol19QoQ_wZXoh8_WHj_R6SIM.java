package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$xzHol19QoQ_wZXoh8_WHj_R6SIM implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ AlertDialog[] f$1;
    private final /* synthetic */ BaseFragment f$2;
    private final /* synthetic */ int f$3;

    public /* synthetic */ -$$Lambda$MessagesController$xzHol19QoQ_wZXoh8_WHj_R6SIM(MessagesController messagesController, AlertDialog[] alertDialogArr, BaseFragment baseFragment, int i) {
        this.f$0 = messagesController;
        this.f$1 = alertDialogArr;
        this.f$2 = baseFragment;
        this.f$3 = i;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$openByUserName$277$MessagesController(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
