package org.telegram.ui.Components;

import android.content.Context;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.JoinCallAlert;

public final /* synthetic */ class JoinCallAlert$$ExternalSyntheticLambda7 implements RequestDelegate {
    public final /* synthetic */ AlertDialog f$0;
    public final /* synthetic */ AccountInstance f$1;
    public final /* synthetic */ JoinCallAlert.JoinCallAlertDelegate f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ Context f$4;
    public final /* synthetic */ BaseFragment f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ TLRPC$Peer f$7;

    public /* synthetic */ JoinCallAlert$$ExternalSyntheticLambda7(AlertDialog alertDialog, AccountInstance accountInstance, JoinCallAlert.JoinCallAlertDelegate joinCallAlertDelegate, long j, Context context, BaseFragment baseFragment, int i, TLRPC$Peer tLRPC$Peer) {
        this.f$0 = alertDialog;
        this.f$1 = accountInstance;
        this.f$2 = joinCallAlertDelegate;
        this.f$3 = j;
        this.f$4 = context;
        this.f$5 = baseFragment;
        this.f$6 = i;
        this.f$7 = tLRPC$Peer;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new JoinCallAlert$$ExternalSyntheticLambda5(this.f$0, tLObject, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7));
    }
}
