package org.telegram.ui.Components;

import android.content.Context;

public final /* synthetic */ class GroupCallPipAlertView$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ Context f$0;

    public /* synthetic */ GroupCallPipAlertView$$ExternalSyntheticLambda4(Context context) {
        this.f$0 = context;
    }

    public final void run() {
        GroupCallPip.updateVisibility(this.f$0);
    }
}
