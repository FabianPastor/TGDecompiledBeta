package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$qAcR4ARhsWqyzh4iuxGrFGyr3io implements Runnable {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ TL_error f$2;
    private final /* synthetic */ TLObject f$3;
    private final /* synthetic */ int f$4;

    public /* synthetic */ -$$Lambda$LaunchActivity$qAcR4ARhsWqyzh4iuxGrFGyr3io(LaunchActivity launchActivity, AlertDialog alertDialog, TL_error tL_error, TLObject tLObject, int i) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
        this.f$2 = tL_error;
        this.f$3 = tLObject;
        this.f$4 = i;
    }

    public final void run() {
        this.f$0.lambda$null$21$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
