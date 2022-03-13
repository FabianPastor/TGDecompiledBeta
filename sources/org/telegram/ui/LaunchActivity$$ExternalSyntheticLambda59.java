package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda59 implements RequestDelegate {
    public final /* synthetic */ AlertDialog f$0;
    public final /* synthetic */ ActionIntroActivity f$1;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda59(AlertDialog alertDialog, ActionIntroActivity actionIntroActivity) {
        this.f$0 = alertDialog;
        this.f$1 = actionIntroActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda21(this.f$0, tLObject, this.f$1, tLRPC$TL_error));
    }
}
