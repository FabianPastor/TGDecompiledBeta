package org.telegram.ui;

import org.telegram.ui.ActionBar.AlertDialog;

final /* synthetic */ class CacheControlActivity$$Lambda$1 implements Runnable {
    private final CacheControlActivity arg$1;
    private final AlertDialog arg$2;

    CacheControlActivity$$Lambda$1(CacheControlActivity cacheControlActivity, AlertDialog alertDialog) {
        this.arg$1 = cacheControlActivity;
        this.arg$2 = alertDialog;
    }

    public void run() {
        this.arg$1.lambda$cleanupFolders$3$CacheControlActivity(this.arg$2);
    }
}
