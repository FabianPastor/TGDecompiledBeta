package org.telegram.ui.Components;

import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$SCrK_EWQcGEC1BMsMG0P334yXtQ implements Runnable {
    private final /* synthetic */ AlertDialog[] f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ BaseFragment f$3;

    public /* synthetic */ -$$Lambda$AlertsCreator$SCrK_EWQcGEC1BMsMG0P334yXtQ(AlertDialog[] alertDialogArr, int i, int i2, BaseFragment baseFragment) {
        this.f$0 = alertDialogArr;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = baseFragment;
    }

    public final void run() {
        AlertsCreator.lambda$createDeleteMessagesAlert$46(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}