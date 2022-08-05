package org.telegram.ui.ActionBar;

import org.telegram.messenger.AndroidUtilities;

public final /* synthetic */ class ActionBarLayout$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ Runnable f$0;

    public /* synthetic */ ActionBarLayout$$ExternalSyntheticLambda0(Runnable runnable) {
        this.f$0 = runnable;
    }

    public final void run() {
        AndroidUtilities.runOnUIThread(this.f$0);
    }
}
