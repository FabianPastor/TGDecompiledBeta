package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ProfileActivity.AnonymousClass3;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ProfileActivity$3$10b11ADJG6tYq3lPsRhNkeDms-U implements RequestDelegate {
    private final /* synthetic */ AnonymousClass3 f$0;
    private final /* synthetic */ AlertDialog[] f$1;

    public /* synthetic */ -$$Lambda$ProfileActivity$3$10b11ADJG6tYq3lPsRhNkeDms-U(AnonymousClass3 anonymousClass3, AlertDialog[] alertDialogArr) {
        this.f$0 = anonymousClass3;
        this.f$1 = alertDialogArr;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$onItemClick$5$ProfileActivity$3(this.f$1, tLObject, tL_error);
    }
}
