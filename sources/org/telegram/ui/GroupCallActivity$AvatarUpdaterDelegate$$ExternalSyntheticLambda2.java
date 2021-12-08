package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.GroupCallActivity;

public final /* synthetic */ class GroupCallActivity$AvatarUpdaterDelegate$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ GroupCallActivity.AvatarUpdaterDelegate f$0;
    public final /* synthetic */ TLRPC.TL_error f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ GroupCallActivity$AvatarUpdaterDelegate$$ExternalSyntheticLambda2(GroupCallActivity.AvatarUpdaterDelegate avatarUpdaterDelegate, TLRPC.TL_error tL_error, TLObject tLObject, String str) {
        this.f$0 = avatarUpdaterDelegate;
        this.f$1 = tL_error;
        this.f$2 = tLObject;
        this.f$3 = str;
    }

    public final void run() {
        this.f$0.m3007x8cb0659e(this.f$1, this.f$2, this.f$3);
    }
}
