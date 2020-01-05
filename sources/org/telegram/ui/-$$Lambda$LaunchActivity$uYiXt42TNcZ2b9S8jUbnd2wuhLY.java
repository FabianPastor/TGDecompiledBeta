package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$uYiXt42TNcZ2b9S8jUbnd2wuhLY implements Runnable {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ AlertDialog f$4;
    private final /* synthetic */ String f$5;

    public /* synthetic */ -$$Lambda$LaunchActivity$uYiXt42TNcZ2b9S8jUbnd2wuhLY(LaunchActivity launchActivity, TL_error tL_error, TLObject tLObject, int i, AlertDialog alertDialog, String str) {
        this.f$0 = launchActivity;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = i;
        this.f$4 = alertDialog;
        this.f$5 = str;
    }

    public final void run() {
        this.f$0.lambda$null$18$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
