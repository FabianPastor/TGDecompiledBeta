package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class DialogsActivity$6$$ExternalSyntheticLambda3 implements RequestDelegate {
    public static final /* synthetic */ DialogsActivity$6$$ExternalSyntheticLambda3 INSTANCE = new DialogsActivity$6$$ExternalSyntheticLambda3();

    private /* synthetic */ DialogsActivity$6$$ExternalSyntheticLambda3() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(DialogsActivity$6$$ExternalSyntheticLambda2.INSTANCE);
    }
}
