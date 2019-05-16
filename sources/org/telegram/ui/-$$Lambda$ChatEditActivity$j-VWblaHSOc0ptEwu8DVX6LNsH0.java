package org.telegram.ui;

import java.util.concurrent.CountDownLatch;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatEditActivity$j-VWblaHSOc0ptEwu8DVX6LNsH0 implements Runnable {
    private final /* synthetic */ ChatEditActivity f$0;
    private final /* synthetic */ CountDownLatch f$1;

    public /* synthetic */ -$$Lambda$ChatEditActivity$j-VWblaHSOc0ptEwu8DVX6LNsH0(ChatEditActivity chatEditActivity, CountDownLatch countDownLatch) {
        this.f$0 = chatEditActivity;
        this.f$1 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$onFragmentCreate$0$ChatEditActivity(this.f$1);
    }
}
