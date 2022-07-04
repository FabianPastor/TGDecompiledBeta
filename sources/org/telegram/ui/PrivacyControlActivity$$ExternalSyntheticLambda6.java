package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class PrivacyControlActivity$$ExternalSyntheticLambda6 implements RequestDelegate {
    public final /* synthetic */ PrivacyControlActivity f$0;
    public final /* synthetic */ AlertDialog f$1;

    public /* synthetic */ PrivacyControlActivity$$ExternalSyntheticLambda6(PrivacyControlActivity privacyControlActivity, AlertDialog alertDialog) {
        this.f$0 = privacyControlActivity;
        this.f$1 = alertDialog;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4360xbb67var_(this.f$1, tLObject, tL_error);
    }
}
