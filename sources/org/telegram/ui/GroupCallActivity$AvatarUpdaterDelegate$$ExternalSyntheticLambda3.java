package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.GroupCallActivity;

public final /* synthetic */ class GroupCallActivity$AvatarUpdaterDelegate$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ GroupCallActivity.AvatarUpdaterDelegate f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ GroupCallActivity$AvatarUpdaterDelegate$$ExternalSyntheticLambda3(GroupCallActivity.AvatarUpdaterDelegate avatarUpdaterDelegate, String str) {
        this.f$0 = avatarUpdaterDelegate;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$didUploadPhoto$1(this.f$1, tLObject, tLRPC$TL_error);
    }
}
