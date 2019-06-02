package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$1VidRY4qKSsVbsBnftG46vcEx-4 implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$ChatActivity$1VidRY4qKSsVbsBnftG46vcEx-4 INSTANCE = new -$$Lambda$ChatActivity$1VidRY4qKSsVbsBnftG46vcEx-4();

    private /* synthetic */ -$$Lambda$ChatActivity$1VidRY4qKSsVbsBnftG46vcEx-4() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ChatActivity$cWCA5x0DAJ_SJsezlQnjVOHPVuQ(tLObject));
    }
}
