package org.telegram.ui;

import java.util.concurrent.CountDownLatch;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatEditTypeActivity$_xE1oN7Z_mocgmUxwzzUt1LjqBE implements Runnable {
    private final /* synthetic */ ChatEditTypeActivity f$0;
    private final /* synthetic */ CountDownLatch f$1;

    public /* synthetic */ -$$Lambda$ChatEditTypeActivity$_xE1oN7Z_mocgmUxwzzUt1LjqBE(ChatEditTypeActivity chatEditTypeActivity, CountDownLatch countDownLatch) {
        this.f$0 = chatEditTypeActivity;
        this.f$1 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$onFragmentCreate$0$ChatEditTypeActivity(this.f$1);
    }
}
