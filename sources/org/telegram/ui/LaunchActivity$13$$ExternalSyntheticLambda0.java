package org.telegram.ui;

import org.telegram.ui.LaunchActivity;

public final /* synthetic */ class LaunchActivity$13$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ LaunchActivity.AnonymousClass13 f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ ChatActivity f$3;

    public /* synthetic */ LaunchActivity$13$$ExternalSyntheticLambda0(LaunchActivity.AnonymousClass13 r1, String str, long j, ChatActivity chatActivity) {
        this.f$0 = r1;
        this.f$1 = str;
        this.f$2 = j;
        this.f$3 = chatActivity;
    }

    public final void run() {
        this.f$0.lambda$onMessagesLoaded$0(this.f$1, this.f$2, this.f$3);
    }
}
