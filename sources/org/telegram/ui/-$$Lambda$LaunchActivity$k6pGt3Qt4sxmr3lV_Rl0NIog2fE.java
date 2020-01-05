package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$k6pGt3Qt4sxmr3lV_Rl0NIog2fE implements RequestDelegate {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ String f$3;
    private final /* synthetic */ String f$4;
    private final /* synthetic */ Integer f$5;
    private final /* synthetic */ AlertDialog f$6;

    public /* synthetic */ -$$Lambda$LaunchActivity$k6pGt3Qt4sxmr3lV_Rl0NIog2fE(LaunchActivity launchActivity, String str, int i, String str2, String str3, Integer num, AlertDialog alertDialog) {
        this.f$0 = launchActivity;
        this.f$1 = str;
        this.f$2 = i;
        this.f$3 = str2;
        this.f$4 = str3;
        this.f$5 = num;
        this.f$6 = alertDialog;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$runLinkRequest$16$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tL_error);
    }
}
