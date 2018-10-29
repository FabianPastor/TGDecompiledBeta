package org.telegram.ui;

import java.util.concurrent.CountDownLatch;

final /* synthetic */ class ProfileActivity$$Lambda$0 implements Runnable {
    private final ProfileActivity arg$1;
    private final CountDownLatch arg$2;

    ProfileActivity$$Lambda$0(ProfileActivity profileActivity, CountDownLatch countDownLatch) {
        this.arg$1 = profileActivity;
        this.arg$2 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$onFragmentCreate$0$ProfileActivity(this.arg$2);
    }
}
