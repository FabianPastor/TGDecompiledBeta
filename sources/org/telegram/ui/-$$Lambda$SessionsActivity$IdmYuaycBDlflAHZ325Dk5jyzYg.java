package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$SessionsActivity$IdmYuaycBDlflAHZ325Dk5jyzYg implements Runnable {
    private final /* synthetic */ SessionsActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ TL_error f$3;

    public /* synthetic */ -$$Lambda$SessionsActivity$IdmYuaycBDlflAHZ325Dk5jyzYg(SessionsActivity sessionsActivity, AlertDialog alertDialog, TLObject tLObject, TL_error tL_error) {
        this.f$0 = sessionsActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLObject;
        this.f$3 = tL_error;
    }

    public final void run() {
        this.f$0.lambda$null$1$SessionsActivity(this.f$1, this.f$2, this.f$3);
    }
}
