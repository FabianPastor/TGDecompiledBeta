package org.telegram.ui.ActionBar;

final /* synthetic */ class AlertDialog$$Lambda$0 implements Runnable {
    private final AlertDialog arg$1;

    AlertDialog$$Lambda$0(AlertDialog alertDialog) {
        this.arg$1 = alertDialog;
    }

    public void run() {
        this.arg$1.dismiss();
    }
}
