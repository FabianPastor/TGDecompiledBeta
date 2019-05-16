package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$iA5EAADkSdqYqWOi8kGrCd8nXKM implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$ChatActivity$iA5EAADkSdqYqWOi8kGrCd8nXKM INSTANCE = new -$$Lambda$ChatActivity$iA5EAADkSdqYqWOi8kGrCd8nXKM();

    private /* synthetic */ -$$Lambda$ChatActivity$iA5EAADkSdqYqWOi8kGrCd8nXKM() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ChatActivity$Y0i_Wm0Y23K6aEiER7lEX_CjFGU(tLObject));
    }
}
