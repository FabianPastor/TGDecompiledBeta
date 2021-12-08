package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PassportActivity;

public final /* synthetic */ class PassportActivity$20$1$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ PassportActivity.ErrorRunnable f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ PassportActivity$20$1$$ExternalSyntheticLambda2(PassportActivity.ErrorRunnable errorRunnable, TLRPC.TL_error tL_error, String str) {
        this.f$0 = errorRunnable;
        this.f$1 = tL_error;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.onError(this.f$1.text, this.f$2);
    }
}
