package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda20 implements Runnable {
    public final /* synthetic */ ActionIntroActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda20(ActionIntroActivity actionIntroActivity, TLRPC.TL_error tL_error) {
        this.f$0 = actionIntroActivity;
        this.f$1 = tL_error;
    }

    public final void run() {
        LaunchActivity.lambda$handleIntent$18(this.f$0, this.f$1);
    }
}
