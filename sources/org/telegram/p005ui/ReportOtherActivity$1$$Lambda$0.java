package org.telegram.p005ui;

import org.telegram.p005ui.ReportOtherActivity.CLASSNAME;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ReportOtherActivity$1$$Lambda$0 */
final /* synthetic */ class ReportOtherActivity$1$$Lambda$0 implements RequestDelegate {
    static final RequestDelegate $instance = new ReportOtherActivity$1$$Lambda$0();

    private ReportOtherActivity$1$$Lambda$0() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        CLASSNAME.lambda$onItemClick$0$ReportOtherActivity$1(tLObject, tL_error);
    }
}
