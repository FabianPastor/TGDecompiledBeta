package org.telegram.messenger.browser;

import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class Browser$$Lambda$1 implements Runnable {
    private final AlertDialog[] arg$1;
    private final int arg$2;

    Browser$$Lambda$1(AlertDialog[] alertDialogArr, int i) {
        this.arg$1 = alertDialogArr;
        this.arg$2 = i;
    }

    public void run() {
        Browser.lambda$openUrl$3$Browser(this.arg$1, this.arg$2);
    }
}
