package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$Aq7QhKQ9ZIbQnCdAP03bqrZTLQM implements Runnable {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ TLObject f$1;
    private final /* synthetic */ AlertDialog f$2;
    private final /* synthetic */ TL_error f$3;

    public /* synthetic */ -$$Lambda$LaunchActivity$Aq7QhKQ9ZIbQnCdAP03bqrZTLQM(LaunchActivity launchActivity, TLObject tLObject, AlertDialog alertDialog, TL_error tL_error) {
        this.f$0 = launchActivity;
        this.f$1 = tLObject;
        this.f$2 = alertDialog;
        this.f$3 = tL_error;
    }

    public final void run() {
        this.f$0.lambda$null$34$LaunchActivity(this.f$1, this.f$2, this.f$3);
    }
}
