package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ReportOtherActivity.AnonymousClass1;

final /* synthetic */ class ReportOtherActivity$1$$Lambda$0 implements RequestDelegate {
    static final RequestDelegate $instance = new ReportOtherActivity$1$$Lambda$0();

    private ReportOtherActivity$1$$Lambda$0() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        AnonymousClass1.lambda$onItemClick$0$ReportOtherActivity$1(tLObject, tL_error);
    }
}
