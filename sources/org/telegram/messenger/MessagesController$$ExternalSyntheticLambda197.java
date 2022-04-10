package org.telegram.messenger;

import android.os.Bundle;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda197 implements Runnable {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ BaseFragment f$3;
    public final /* synthetic */ Bundle f$4;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda197(MessagesController messagesController, AlertDialog alertDialog, TLObject tLObject, BaseFragment baseFragment, Bundle bundle) {
        this.f$0 = messagesController;
        this.f$1 = alertDialog;
        this.f$2 = tLObject;
        this.f$3 = baseFragment;
        this.f$4 = bundle;
    }

    public final void run() {
        this.f$0.lambda$checkCanOpenChat$335(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
