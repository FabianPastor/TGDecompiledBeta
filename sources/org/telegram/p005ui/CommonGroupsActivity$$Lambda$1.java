package org.telegram.p005ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.CommonGroupsActivity$$Lambda$1 */
final /* synthetic */ class CommonGroupsActivity$$Lambda$1 implements RequestDelegate {
    private final CommonGroupsActivity arg$1;
    private final int arg$2;

    CommonGroupsActivity$$Lambda$1(CommonGroupsActivity commonGroupsActivity, int i) {
        this.arg$1 = commonGroupsActivity;
        this.arg$2 = i;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$getChats$2$CommonGroupsActivity(this.arg$2, tLObject, tL_error);
    }
}
