package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda50 implements RequestDelegate {
    public final /* synthetic */ AlertDialog f$0;
    public final /* synthetic */ ActionIntroActivity f$1;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda50(AlertDialog alertDialog, ActionIntroActivity actionIntroActivity) {
        this.f$0 = alertDialog;
        this.f$1 = actionIntroActivity;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda12(this.f$0, tLObject, this.f$1, tL_error));
    }
}
