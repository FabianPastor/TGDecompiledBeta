package org.telegram.ui.Components;

import android.content.SharedPreferences;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class AlertsCreator$$Lambda$6 implements RequestDelegate {
    private final SharedPreferences arg$1;
    private final AlertDialog arg$2;
    private final int arg$3;
    private final BaseFragment arg$4;

    AlertsCreator$$Lambda$6(SharedPreferences sharedPreferences, AlertDialog alertDialog, int i, BaseFragment baseFragment) {
        this.arg$1 = sharedPreferences;
        this.arg$2 = alertDialog;
        this.arg$3 = i;
        this.arg$4 = baseFragment;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        AlertsCreator.lambda$performAskAQuestion$8$AlertsCreator(this.arg$1, this.arg$2, this.arg$3, this.arg$4, tLObject, tL_error);
    }
}
