package org.telegram.ui;

import java.util.concurrent.CountDownLatch;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProfileActivity$ELtZVLkMORk4xUSfHIm0LgPewE8 implements Runnable {
    private final /* synthetic */ ProfileActivity f$0;
    private final /* synthetic */ CountDownLatch f$1;

    public /* synthetic */ -$$Lambda$ProfileActivity$ELtZVLkMORk4xUSfHIm0LgPewE8(ProfileActivity profileActivity, CountDownLatch countDownLatch) {
        this.f$0 = profileActivity;
        this.f$1 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$onFragmentCreate$0$ProfileActivity(this.f$1);
    }
}
