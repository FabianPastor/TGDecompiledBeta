package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda29 implements RequestDelegate {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda29(ProfileActivity profileActivity, String str) {
        this.f$0 = profileActivity;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$didUploadPhoto$30(this.f$1, tLObject, tLRPC$TL_error);
    }
}