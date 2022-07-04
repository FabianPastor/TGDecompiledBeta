package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda32 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC.Chat f$2;
    public final /* synthetic */ DialogsActivity f$3;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda32(LaunchActivity launchActivity, int i, TLRPC.Chat chat, DialogsActivity dialogsActivity) {
        this.f$0 = launchActivity;
        this.f$1 = i;
        this.f$2 = chat;
        this.f$3 = dialogsActivity;
    }

    public final void run() {
        this.f$0.m3670lambda$runLinkRequest$38$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3);
    }
}
