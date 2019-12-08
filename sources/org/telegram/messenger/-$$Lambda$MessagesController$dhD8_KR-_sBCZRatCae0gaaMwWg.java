package org.telegram.messenger;

import android.os.Bundle;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$dhD8_KR-_sBCZRatCae0gaaMwWg implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ BaseFragment f$3;
    private final /* synthetic */ Bundle f$4;

    public /* synthetic */ -$$Lambda$MessagesController$dhD8_KR-_sBCZRatCae0gaaMwWg(MessagesController messagesController, AlertDialog alertDialog, TLObject tLObject, BaseFragment baseFragment, Bundle bundle) {
        this.f$0 = messagesController;
        this.f$1 = alertDialog;
        this.f$2 = tLObject;
        this.f$3 = baseFragment;
        this.f$4 = bundle;
    }

    public final void run() {
        this.f$0.lambda$null$273$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
