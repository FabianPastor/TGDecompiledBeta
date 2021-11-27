package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_authorization;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.SessionsActivity;

public final /* synthetic */ class SessionsActivity$4$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ SessionsActivity.AnonymousClass4 f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLRPC$TL_authorization f$2;

    public /* synthetic */ SessionsActivity$4$$ExternalSyntheticLambda1(SessionsActivity.AnonymousClass4 r1, AlertDialog alertDialog, TLRPC$TL_authorization tLRPC$TL_authorization) {
        this.f$0 = r1;
        this.f$1 = alertDialog;
        this.f$2 = tLRPC$TL_authorization;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onSessionTerminated$1(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
