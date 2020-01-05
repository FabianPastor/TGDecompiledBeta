package org.telegram.ui.ActionBar;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$Theme$axfOWeRl7QketJxdsxdMlUbAw4w implements RequestDelegate {
    private final /* synthetic */ int f$0;

    public /* synthetic */ -$$Lambda$Theme$axfOWeRl7QketJxdsxdMlUbAw4w(int i) {
        this.f$0 = i;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$Theme$-efrHSLCvxtxdMlKv07p7FeSCM8(this.f$0, tLObject));
    }
}
