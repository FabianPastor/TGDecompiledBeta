package org.telegram.ui.Components;

import android.content.SharedPreferences;
import org.telegram.tgnet.TLRPC.TL_help_support;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class AlertsCreator$$Lambda$47 implements Runnable {
    private final SharedPreferences arg$1;
    private final TL_help_support arg$2;
    private final AlertDialog arg$3;
    private final int arg$4;
    private final BaseFragment arg$5;

    AlertsCreator$$Lambda$47(SharedPreferences sharedPreferences, TL_help_support tL_help_support, AlertDialog alertDialog, int i, BaseFragment baseFragment) {
        this.arg$1 = sharedPreferences;
        this.arg$2 = tL_help_support;
        this.arg$3 = alertDialog;
        this.arg$4 = i;
        this.arg$5 = baseFragment;
    }

    public void run() {
        AlertsCreator.lambda$null$6$AlertsCreator(this.arg$1, this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
