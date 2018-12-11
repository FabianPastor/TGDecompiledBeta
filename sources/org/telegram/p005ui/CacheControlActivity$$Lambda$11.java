package org.telegram.p005ui;

import org.telegram.p005ui.ActionBar.AlertDialog;

/* renamed from: org.telegram.ui.CacheControlActivity$$Lambda$11 */
final /* synthetic */ class CacheControlActivity$$Lambda$11 implements Runnable {
    private final CacheControlActivity arg$1;
    private final boolean arg$2;
    private final AlertDialog arg$3;

    CacheControlActivity$$Lambda$11(CacheControlActivity cacheControlActivity, boolean z, AlertDialog alertDialog) {
        this.arg$1 = cacheControlActivity;
        this.arg$2 = z;
        this.arg$3 = alertDialog;
    }

    public void run() {
        this.arg$1.lambda$null$2$CacheControlActivity(this.arg$2, this.arg$3);
    }
}
