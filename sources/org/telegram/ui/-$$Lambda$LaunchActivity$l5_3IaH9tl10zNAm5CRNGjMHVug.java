package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$l5_3IaH9tl10zNAm5CRNGjMHVug implements RequestDelegate {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ BaseFragment f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ Bundle f$4;

    public /* synthetic */ -$$Lambda$LaunchActivity$l5_3IaH9tl10zNAm5CRNGjMHVug(LaunchActivity launchActivity, AlertDialog alertDialog, BaseFragment baseFragment, int i, Bundle bundle) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
        this.f$2 = baseFragment;
        this.f$3 = i;
        this.f$4 = bundle;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$38$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
