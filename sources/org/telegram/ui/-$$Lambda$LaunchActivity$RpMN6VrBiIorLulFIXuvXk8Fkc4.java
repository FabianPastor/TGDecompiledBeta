package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$RpMN6VrBiIorLulFIXuvXk8Fkc4 implements RequestDelegate {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ String f$4;
    private final /* synthetic */ String f$5;
    private final /* synthetic */ Integer f$6;

    public /* synthetic */ -$$Lambda$LaunchActivity$RpMN6VrBiIorLulFIXuvXk8Fkc4(LaunchActivity launchActivity, AlertDialog alertDialog, String str, int i, String str2, String str3, Integer num) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
        this.f$2 = str;
        this.f$3 = i;
        this.f$4 = str2;
        this.f$5 = str3;
        this.f$6 = num;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$runLinkRequest$12$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, tLObject, tL_error);
    }
}
