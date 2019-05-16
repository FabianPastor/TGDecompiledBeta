package org.telegram.ui;

import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserFull;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChangeBioActivity$tNpkkFgAdE6xw7aDpwDOLm8jmpc implements Runnable {
    private final /* synthetic */ ChangeBioActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ UserFull f$2;
    private final /* synthetic */ String f$3;
    private final /* synthetic */ User f$4;

    public /* synthetic */ -$$Lambda$ChangeBioActivity$tNpkkFgAdE6xw7aDpwDOLm8jmpc(ChangeBioActivity changeBioActivity, AlertDialog alertDialog, UserFull userFull, String str, User user) {
        this.f$0 = changeBioActivity;
        this.f$1 = alertDialog;
        this.f$2 = userFull;
        this.f$3 = str;
        this.f$4 = user;
    }

    public final void run() {
        this.f$0.lambda$null$2$ChangeBioActivity(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
