package org.telegram.ui;

import java.util.concurrent.CountDownLatch;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda17 implements Runnable {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ CountDownLatch f$1;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda17(ProfileActivity profileActivity, CountDownLatch countDownLatch) {
        this.f$0 = profileActivity;
        this.f$1 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$onFragmentCreate$0(this.f$1);
    }
}
