package org.telegram.ui;

import org.telegram.ui.CallLogActivity.AnonymousClass3;

final /* synthetic */ class CallLogActivity$3$$Lambda$0 implements Runnable {
    private final AnonymousClass3 arg$1;
    private final CallLogRow arg$2;

    CallLogActivity$3$$Lambda$0(AnonymousClass3 anonymousClass3, CallLogRow callLogRow) {
        this.arg$1 = anonymousClass3;
        this.arg$2 = callLogRow;
    }

    public void run() {
        this.arg$1.lambda$onScrolled$0$CallLogActivity$3(this.arg$2);
    }
}
