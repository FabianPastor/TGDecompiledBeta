package org.telegram.ui;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$GroupCreateFinalActivity$eKyq7cJMgDwCwGYOq_2k3JE_l-A implements Runnable {
    private final /* synthetic */ GroupCreateFinalActivity f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ CountDownLatch f$3;

    public /* synthetic */ -$$Lambda$GroupCreateFinalActivity$eKyq7cJMgDwCwGYOq_2k3JE_l-A(GroupCreateFinalActivity groupCreateFinalActivity, ArrayList arrayList, ArrayList arrayList2, CountDownLatch countDownLatch) {
        this.f$0 = groupCreateFinalActivity;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
        this.f$3 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$onFragmentCreate$0$GroupCreateFinalActivity(this.f$1, this.f$2, this.f$3);
    }
}
