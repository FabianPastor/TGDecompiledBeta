package org.telegram.ui.Components.voip;

import android.app.Activity;
import org.telegram.tgnet.TLRPC.User;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$VoIPHelper$FLpKQd1yLbCCIVZIjPmbCXMk1eM implements Runnable {
    private final /* synthetic */ User f$0;
    private final /* synthetic */ Activity f$1;

    public /* synthetic */ -$$Lambda$VoIPHelper$FLpKQd1yLbCCIVZIjPmbCXMk1eM(User user, Activity activity) {
        this.f$0 = user;
        this.f$1 = activity;
    }

    public final void run() {
        VoIPHelper.doInitiateCall(this.f$0, this.f$1);
    }
}
