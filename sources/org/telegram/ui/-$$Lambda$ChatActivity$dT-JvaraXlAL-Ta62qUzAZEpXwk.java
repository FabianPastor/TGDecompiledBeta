package org.telegram.ui;

import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$dT-JvaraXlAL-Ta62qUzAZEpXwk implements Runnable {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ AlertDialog[] f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$ChatActivity$dT-JvaraXlAL-Ta62qUzAZEpXwk(ChatActivity chatActivity, AlertDialog[] alertDialogArr, int i) {
        this.f$0 = chatActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$processSelectedOption$84$ChatActivity(this.f$1, this.f$2);
    }
}