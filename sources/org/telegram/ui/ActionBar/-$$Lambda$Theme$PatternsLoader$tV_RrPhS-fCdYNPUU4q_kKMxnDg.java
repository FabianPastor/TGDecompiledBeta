package org.telegram.ui.ActionBar;

import org.telegram.ui.ActionBar.Theme.PatternsLoader;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$Theme$PatternsLoader$tV_RrPhS-fCdYNPUU4q_kKMxnDg implements Runnable {
    private final /* synthetic */ PatternsLoader f$0;
    private final /* synthetic */ LoadingPattern f$1;

    public /* synthetic */ -$$Lambda$Theme$PatternsLoader$tV_RrPhS-fCdYNPUU4q_kKMxnDg(PatternsLoader patternsLoader, LoadingPattern loadingPattern) {
        this.f$0 = patternsLoader;
        this.f$1 = loadingPattern;
    }

    public final void run() {
        this.f$0.lambda$didReceivedNotification$3$Theme$PatternsLoader(this.f$1);
    }
}
