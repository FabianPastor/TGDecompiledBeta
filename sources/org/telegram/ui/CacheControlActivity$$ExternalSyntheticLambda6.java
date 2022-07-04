package org.telegram.ui;

import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class CacheControlActivity$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ CacheControlActivity f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ AlertDialog f$2;
    public final /* synthetic */ long f$3;

    public /* synthetic */ CacheControlActivity$$ExternalSyntheticLambda6(CacheControlActivity cacheControlActivity, boolean z, AlertDialog alertDialog, long j) {
        this.f$0 = cacheControlActivity;
        this.f$1 = z;
        this.f$2 = alertDialog;
        this.f$3 = j;
    }

    public final void run() {
        this.f$0.lambda$cleanupFolders$2(this.f$1, this.f$2, this.f$3);
    }
}
