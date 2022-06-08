package org.telegram.ui;

import org.telegram.tgnet.TLRPC$Chat;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda35 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC$Chat f$2;
    public final /* synthetic */ DialogsActivity f$3;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda35(LaunchActivity launchActivity, int i, TLRPC$Chat tLRPC$Chat, DialogsActivity dialogsActivity) {
        this.f$0 = launchActivity;
        this.f$1 = i;
        this.f$2 = tLRPC$Chat;
        this.f$3 = dialogsActivity;
    }

    public final void run() {
        this.f$0.lambda$runLinkRequest$38(this.f$1, this.f$2, this.f$3);
    }
}
