package org.telegram.ui.ActionBar;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$Theme$W8PxHsNGN-1ClSKhdG5WIm1ySCw implements RequestDelegate {
    private final /* synthetic */ int f$0;

    public /* synthetic */ -$$Lambda$Theme$W8PxHsNGN-1ClSKhdG5WIm1ySCw(int i) {
        this.f$0 = i;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$Theme$5ndE3I7wLM7SEX0BSviAukVkBYc(tLObject, this.f$0));
    }
}
