package org.telegram.ui.Components;

import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class JoinCallAlert$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ AlertDialog f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ AccountInstance f$3;
    public final /* synthetic */ MessagesStorage.BooleanCallback f$4;

    public /* synthetic */ JoinCallAlert$$ExternalSyntheticLambda4(AlertDialog alertDialog, TLObject tLObject, long j, AccountInstance accountInstance, MessagesStorage.BooleanCallback booleanCallback) {
        this.f$0 = alertDialog;
        this.f$1 = tLObject;
        this.f$2 = j;
        this.f$3 = accountInstance;
        this.f$4 = booleanCallback;
    }

    public final void run() {
        JoinCallAlert.lambda$checkFewUsers$0(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
