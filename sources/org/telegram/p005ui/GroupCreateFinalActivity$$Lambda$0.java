package org.telegram.p005ui;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

/* renamed from: org.telegram.ui.GroupCreateFinalActivity$$Lambda$0 */
final /* synthetic */ class GroupCreateFinalActivity$$Lambda$0 implements Runnable {
    private final GroupCreateFinalActivity arg$1;
    private final ArrayList arg$2;
    private final ArrayList arg$3;
    private final CountDownLatch arg$4;

    GroupCreateFinalActivity$$Lambda$0(GroupCreateFinalActivity groupCreateFinalActivity, ArrayList arrayList, ArrayList arrayList2, CountDownLatch countDownLatch) {
        this.arg$1 = groupCreateFinalActivity;
        this.arg$2 = arrayList;
        this.arg$3 = arrayList2;
        this.arg$4 = countDownLatch;
    }

    public void run() {
        this.arg$1.lambda$onFragmentCreate$0$GroupCreateFinalActivity(this.arg$2, this.arg$3, this.arg$4);
    }
}
