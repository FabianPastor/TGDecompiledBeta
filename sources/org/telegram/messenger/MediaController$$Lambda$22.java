package org.telegram.messenger;

import org.telegram.p005ui.ActionBar.AlertDialog;

final /* synthetic */ class MediaController$$Lambda$22 implements Runnable {
    private final AlertDialog arg$1;
    private final int arg$2;

    MediaController$$Lambda$22(AlertDialog alertDialog, int i) {
        this.arg$1 = alertDialog;
        this.arg$2 = i;
    }

    public void run() {
        MediaController.lambda$null$24$MediaController(this.arg$1, this.arg$2);
    }
}
