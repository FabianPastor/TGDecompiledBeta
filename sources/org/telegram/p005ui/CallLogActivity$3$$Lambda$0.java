package org.telegram.p005ui;

import org.telegram.p005ui.CallLogActivity.CLASSNAME;
import org.telegram.p005ui.CallLogActivity.CallLogRow;

/* renamed from: org.telegram.ui.CallLogActivity$3$$Lambda$0 */
final /* synthetic */ class CallLogActivity$3$$Lambda$0 implements Runnable {
    private final CLASSNAME arg$1;
    private final CallLogRow arg$2;

    CallLogActivity$3$$Lambda$0(CLASSNAME CLASSNAME, CallLogRow callLogRow) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = callLogRow;
    }

    public void run() {
        this.arg$1.lambda$onScrolled$0$CallLogActivity$3(this.arg$2);
    }
}
