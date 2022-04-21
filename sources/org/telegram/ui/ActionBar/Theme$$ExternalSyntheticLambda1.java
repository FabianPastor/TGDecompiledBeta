package org.telegram.ui.ActionBar;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class Theme$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ int f$0;

    public /* synthetic */ Theme$$ExternalSyntheticLambda1(int i) {
        this.f$0 = i;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Theme$$ExternalSyntheticLambda0(this.f$0, tLObject));
    }
}
