package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$m0WdBFNUrZDToj2GiVi4czfOzdI implements RequestDelegate {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ String f$3;

    public /* synthetic */ -$$Lambda$LaunchActivity$m0WdBFNUrZDToj2GiVi4czfOzdI(LaunchActivity launchActivity, AlertDialog alertDialog, int i, String str) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
        this.f$2 = i;
        this.f$3 = str;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$runLinkRequest$16$LaunchActivity(this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}
