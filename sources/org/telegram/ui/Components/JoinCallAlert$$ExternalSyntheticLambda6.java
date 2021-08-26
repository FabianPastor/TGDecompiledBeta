package org.telegram.ui.Components;

import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class JoinCallAlert$$ExternalSyntheticLambda6 implements RequestDelegate {
    public final /* synthetic */ AlertDialog f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ AccountInstance f$2;
    public final /* synthetic */ MessagesStorage.BooleanCallback f$3;

    public /* synthetic */ JoinCallAlert$$ExternalSyntheticLambda6(AlertDialog alertDialog, int i, AccountInstance accountInstance, MessagesStorage.BooleanCallback booleanCallback) {
        this.f$0 = alertDialog;
        this.f$1 = i;
        this.f$2 = accountInstance;
        this.f$3 = booleanCallback;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new JoinCallAlert$$ExternalSyntheticLambda4(this.f$0, tLObject, this.f$1, this.f$2, this.f$3));
    }
}
