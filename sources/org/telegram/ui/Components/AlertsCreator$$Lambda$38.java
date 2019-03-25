package org.telegram.ui.Components;

import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class AlertsCreator$$Lambda$38 implements Runnable {
    private final AlertDialog[] arg$1;
    private final int arg$2;
    private final int arg$3;
    private final BaseFragment arg$4;

    AlertsCreator$$Lambda$38(AlertDialog[] alertDialogArr, int i, int i2, BaseFragment baseFragment) {
        this.arg$1 = alertDialogArr;
        this.arg$2 = i;
        this.arg$3 = i2;
        this.arg$4 = baseFragment;
    }

    public void run() {
        AlertsCreator.lambda$createDeleteMessagesAlert$43$AlertsCreator(this.arg$1, this.arg$2, this.arg$3, this.arg$4);
    }
}
