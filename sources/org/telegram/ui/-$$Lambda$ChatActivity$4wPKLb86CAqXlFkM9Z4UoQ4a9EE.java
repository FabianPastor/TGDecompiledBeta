package org.telegram.ui;

import org.telegram.ui.ActionBar.AlertDialog;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$4wPKLb86CAqXlFkM9Z4UoQ4a9EE implements Runnable {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ AlertDialog[] f$1;
    private final /* synthetic */ int f$2;

    public /* synthetic */ -$$Lambda$ChatActivity$4wPKLb86CAqXlFkM9Z4UoQ4a9EE(ChatActivity chatActivity, AlertDialog[] alertDialogArr, int i) {
        this.f$0 = chatActivity;
        this.f$1 = alertDialogArr;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$processSelectedOption$77$ChatActivity(this.f$1, this.f$2);
    }
}