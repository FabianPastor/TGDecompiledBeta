package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes3.dex */
public final /* synthetic */ class TermsOfServiceView$$ExternalSyntheticLambda7 implements RequestDelegate {
    public static final /* synthetic */ TermsOfServiceView$$ExternalSyntheticLambda7 INSTANCE = new TermsOfServiceView$$ExternalSyntheticLambda7();

    private /* synthetic */ TermsOfServiceView$$ExternalSyntheticLambda7() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TermsOfServiceView.lambda$accept$7(tLObject, tLRPC$TL_error);
    }
}
