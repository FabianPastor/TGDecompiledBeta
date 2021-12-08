package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class FilterCreateActivity$$ExternalSyntheticLambda14 implements RequestDelegate {
    public final /* synthetic */ FilterCreateActivity f$0;
    public final /* synthetic */ AlertDialog f$1;

    public /* synthetic */ FilterCreateActivity$$ExternalSyntheticLambda14(FilterCreateActivity filterCreateActivity, AlertDialog alertDialog) {
        this.f$0 = filterCreateActivity;
        this.f$1 = alertDialog;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2911lambda$createView$2$orgtelegramuiFilterCreateActivity(this.f$1, tLObject, tL_error);
    }
}
