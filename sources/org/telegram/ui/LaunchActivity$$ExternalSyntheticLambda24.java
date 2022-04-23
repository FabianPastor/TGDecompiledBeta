package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda24 implements Runnable {
    public final /* synthetic */ ActionIntroActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda24(ActionIntroActivity actionIntroActivity, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0 = actionIntroActivity;
        this.f$1 = tLRPC$TL_error;
    }

    public final void run() {
        LaunchActivity.lambda$handleIntent$17(this.f$0, this.f$1);
    }
}
