package org.telegram.p005ui.ActionBar;

/* renamed from: org.telegram.ui.ActionBar.AlertDialog$$Lambda$0 */
final /* synthetic */ class AlertDialog$$Lambda$0 implements Runnable {
    private final AlertDialog arg$1;

    AlertDialog$$Lambda$0(AlertDialog alertDialog) {
        this.arg$1 = alertDialog;
    }

    public void run() {
        this.arg$1.dismiss();
    }
}
