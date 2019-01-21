package org.telegram.messenger;

import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class MediaController$$Lambda$21 implements Runnable {
    private final AlertDialog arg$1;
    private final int arg$2;

    MediaController$$Lambda$21(AlertDialog alertDialog, int i) {
        this.arg$1 = alertDialog;
        this.arg$2 = i;
    }

    public void run() {
        MediaController.lambda$null$23$MediaController(this.arg$1, this.arg$2);
    }
}
