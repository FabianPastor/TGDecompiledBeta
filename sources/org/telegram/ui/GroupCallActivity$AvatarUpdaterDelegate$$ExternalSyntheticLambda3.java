package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.GroupCallActivity;

public final /* synthetic */ class GroupCallActivity$AvatarUpdaterDelegate$$ExternalSyntheticLambda3 implements RequestDelegate {
    public final /* synthetic */ GroupCallActivity.AvatarUpdaterDelegate f$0;
    public final /* synthetic */ String f$1;

    public /* synthetic */ GroupCallActivity$AvatarUpdaterDelegate$$ExternalSyntheticLambda3(GroupCallActivity.AvatarUpdaterDelegate avatarUpdaterDelegate, String str) {
        this.f$0 = avatarUpdaterDelegate;
        this.f$1 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3008xb2446e9f(this.f$1, tLObject, tL_error);
    }
}
