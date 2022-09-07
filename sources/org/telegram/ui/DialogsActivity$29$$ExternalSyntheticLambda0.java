package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.DialogsActivity;

public final /* synthetic */ class DialogsActivity$29$$ExternalSyntheticLambda0 implements RequestDelegate {
    public static final /* synthetic */ DialogsActivity$29$$ExternalSyntheticLambda0 INSTANCE = new DialogsActivity$29$$ExternalSyntheticLambda0();

    private /* synthetic */ DialogsActivity$29$$ExternalSyntheticLambda0() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        DialogsActivity.AnonymousClass29.lambda$onEmojiSelected$0(tLObject, tLRPC$TL_error);
    }
}
