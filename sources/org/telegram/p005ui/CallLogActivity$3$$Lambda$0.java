package org.telegram.p005ui;

import org.telegram.p005ui.CallLogActivity.C12663;
import org.telegram.p005ui.CallLogActivity.CallLogRow;

/* renamed from: org.telegram.ui.CallLogActivity$3$$Lambda$0 */
final /* synthetic */ class CallLogActivity$3$$Lambda$0 implements Runnable {
    private final C12663 arg$1;
    private final CallLogRow arg$2;

    CallLogActivity$3$$Lambda$0(C12663 c12663, CallLogRow callLogRow) {
        this.arg$1 = c12663;
        this.arg$2 = callLogRow;
    }

    public void run() {
        this.arg$1.lambda$onScrolled$0$CallLogActivity$3(this.arg$2);
    }
}
