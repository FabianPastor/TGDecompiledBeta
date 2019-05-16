package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$iffzHHAMAuBNKcUpOYSEoUIrEeY implements Runnable {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ TLObject f$2;
    private final /* synthetic */ TL_error f$3;
    private final /* synthetic */ String f$4;
    private final /* synthetic */ int f$5;
    private final /* synthetic */ String f$6;
    private final /* synthetic */ String f$7;
    private final /* synthetic */ Integer f$8;

    public /* synthetic */ -$$Lambda$LaunchActivity$iffzHHAMAuBNKcUpOYSEoUIrEeY(LaunchActivity launchActivity, AlertDialog alertDialog, TLObject tLObject, TL_error tL_error, String str, int i, String str2, String str3, Integer num) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLObject;
        this.f$3 = tL_error;
        this.f$4 = str;
        this.f$5 = i;
        this.f$6 = str2;
        this.f$7 = str3;
        this.f$8 = num;
    }

    public final void run() {
        this.f$0.lambda$null$11$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
