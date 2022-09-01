package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ProfileActivity;

public final /* synthetic */ class ProfileActivity$28$$ExternalSyntheticLambda0 implements RequestDelegate {
    public static final /* synthetic */ ProfileActivity$28$$ExternalSyntheticLambda0 INSTANCE = new ProfileActivity$28$$ExternalSyntheticLambda0();

    private /* synthetic */ ProfileActivity$28$$ExternalSyntheticLambda0() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ProfileActivity.AnonymousClass28.lambda$onEmojiSelected$0(tLObject, tLRPC$TL_error);
    }
}
