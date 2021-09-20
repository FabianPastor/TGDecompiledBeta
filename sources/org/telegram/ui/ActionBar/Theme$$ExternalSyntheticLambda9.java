package org.telegram.ui.ActionBar;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class Theme$$ExternalSyntheticLambda9 implements RequestDelegate {
    public final /* synthetic */ int f$0;

    public /* synthetic */ Theme$$ExternalSyntheticLambda9(int i) {
        this.f$0 = i;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Theme$$ExternalSyntheticLambda0(this.f$0, tLObject));
    }
}
