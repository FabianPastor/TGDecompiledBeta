package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda25 implements Runnable {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ TLRPC$TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda25(ProfileActivity profileActivity, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str) {
        this.f$0 = profileActivity;
        this.f$1 = tLRPC$TL_error;
        this.f$2 = tLObject;
        this.f$3 = str;
    }

    public final void run() {
        this.f$0.lambda$didUploadPhoto$37(this.f$1, this.f$2, this.f$3);
    }
}
