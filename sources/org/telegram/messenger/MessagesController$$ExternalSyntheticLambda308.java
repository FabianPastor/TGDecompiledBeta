package org.telegram.messenger;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda308 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ BaseFragment f$2;
    public final /* synthetic */ Bundle f$3;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda308(MessagesController messagesController, AlertDialog alertDialog, BaseFragment baseFragment, Bundle bundle) {
        this.f$0 = messagesController;
        this.f$1 = alertDialog;
        this.f$2 = baseFragment;
        this.f$3 = bundle;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$checkCanOpenChat$324(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
    }
}
