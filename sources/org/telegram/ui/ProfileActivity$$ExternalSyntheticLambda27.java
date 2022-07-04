package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ProfileActivity$$ExternalSyntheticLambda27 implements RequestDelegate {
    public final /* synthetic */ ProfileActivity f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ ProfileActivity$$ExternalSyntheticLambda27(ProfileActivity profileActivity, String str) {
        this.f$0 = profileActivity;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m4404lambda$didUploadPhoto$35$orgtelegramuiProfileActivity(this.f$1, tLObject, tL_error);
    }
}
