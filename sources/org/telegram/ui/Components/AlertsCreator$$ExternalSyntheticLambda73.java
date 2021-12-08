package org.telegram.ui.Components;

import android.content.SharedPreferences;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda73 implements Runnable {
    public final /* synthetic */ SharedPreferences f$0;
    public final /* synthetic */ TLRPC.TL_help_support f$1;
    public final /* synthetic */ AlertDialog f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ BaseFragment f$4;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda73(SharedPreferences sharedPreferences, TLRPC.TL_help_support tL_help_support, AlertDialog alertDialog, int i, BaseFragment baseFragment) {
        this.f$0 = sharedPreferences;
        this.f$1 = tL_help_support;
        this.f$2 = alertDialog;
        this.f$3 = i;
        this.f$4 = baseFragment;
    }

    public final void run() {
        AlertsCreator.lambda$performAskAQuestion$12(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
