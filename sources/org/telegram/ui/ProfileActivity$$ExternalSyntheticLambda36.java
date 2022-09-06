package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda36 implements RequestDelegate {
    public final /* synthetic */ ProfileActivity f$0;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda36(ProfileActivity profileActivity) {
        this.f$0 = profileActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$onFragmentCreate$1(tLObject, tLRPC$TL_error);
    }
}
