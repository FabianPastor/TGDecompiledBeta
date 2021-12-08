package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda13 implements Runnable {
    public final /* synthetic */ ActionIntroActivity f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda13(ActionIntroActivity actionIntroActivity, TLRPC.TL_error tL_error) {
        this.f$0 = actionIntroActivity;
        this.f$1 = tL_error;
    }

    public final void run() {
        LaunchActivity.lambda$handleIntent$14(this.f$0, this.f$1);
    }
}
