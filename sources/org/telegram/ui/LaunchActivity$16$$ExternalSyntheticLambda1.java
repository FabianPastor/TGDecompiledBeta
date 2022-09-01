package org.telegram.ui;

import org.telegram.messenger.AccountInstance;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.LaunchActivity;

public final /* synthetic */ class LaunchActivity$16$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ LaunchActivity.AnonymousClass16 f$0;
    public final /* synthetic */ AccountInstance f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ BaseFragment f$3;

    public /* synthetic */ LaunchActivity$16$$ExternalSyntheticLambda1(LaunchActivity.AnonymousClass16 r1, AccountInstance accountInstance, long j, BaseFragment baseFragment) {
        this.f$0 = r1;
        this.f$1 = accountInstance;
        this.f$2 = j;
        this.f$3 = baseFragment;
    }

    public final void run() {
        this.f$0.lambda$onMessagesLoaded$0(this.f$1, this.f$2, this.f$3);
    }
}
