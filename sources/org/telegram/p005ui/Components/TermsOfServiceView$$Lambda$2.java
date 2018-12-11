package org.telegram.p005ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.Components.TermsOfServiceView$$Lambda$2 */
final /* synthetic */ class TermsOfServiceView$$Lambda$2 implements RequestDelegate {
    static final RequestDelegate $instance = new TermsOfServiceView$$Lambda$2();

    private TermsOfServiceView$$Lambda$2() {
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        TermsOfServiceView.lambda$accept$7$TermsOfServiceView(tLObject, tL_error);
    }
}
