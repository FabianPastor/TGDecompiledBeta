package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PrivacyControlActivity$GPNnel5EWY68dTbf_d3WaxLY8RA implements RequestDelegate {
    private final /* synthetic */ PrivacyControlActivity f$0;

    public /* synthetic */ -$$Lambda$PrivacyControlActivity$GPNnel5EWY68dTbf_d3WaxLY8RA(PrivacyControlActivity privacyControlActivity) {
        this.f$0 = privacyControlActivity;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$applyCurrentPrivacySettings$4$PrivacyControlActivity(tLObject, tL_error);
    }
}
