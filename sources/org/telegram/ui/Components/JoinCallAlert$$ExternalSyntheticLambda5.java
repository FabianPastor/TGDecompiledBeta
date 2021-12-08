package org.telegram.ui.Components;

import android.content.Context;
import org.telegram.messenger.AccountInstance;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.JoinCallAlert;

public final /* synthetic */ class JoinCallAlert$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ AlertDialog f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ AccountInstance f$2;
    public final /* synthetic */ JoinCallAlert.JoinCallAlertDelegate f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ Context f$5;
    public final /* synthetic */ BaseFragment f$6;
    public final /* synthetic */ int f$7;
    public final /* synthetic */ TLRPC$Peer f$8;

    public /* synthetic */ JoinCallAlert$$ExternalSyntheticLambda5(AlertDialog alertDialog, TLObject tLObject, AccountInstance accountInstance, JoinCallAlert.JoinCallAlertDelegate joinCallAlertDelegate, long j, Context context, BaseFragment baseFragment, int i, TLRPC$Peer tLRPC$Peer) {
        this.f$0 = alertDialog;
        this.f$1 = tLObject;
        this.f$2 = accountInstance;
        this.f$3 = joinCallAlertDelegate;
        this.f$4 = j;
        this.f$5 = context;
        this.f$6 = baseFragment;
        this.f$7 = i;
        this.f$8 = tLRPC$Peer;
    }

    public final void run() {
        JoinCallAlert.lambda$open$3(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
