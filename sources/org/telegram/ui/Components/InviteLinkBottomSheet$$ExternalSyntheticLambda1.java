package org.telegram.ui.Components;

import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class InviteLinkBottomSheet$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ InviteLinkBottomSheet f$0;
    public final /* synthetic */ TLRPC$User f$1;
    public final /* synthetic */ BaseFragment f$2;

    public /* synthetic */ InviteLinkBottomSheet$$ExternalSyntheticLambda1(InviteLinkBottomSheet inviteLinkBottomSheet, TLRPC$User tLRPC$User, BaseFragment baseFragment) {
        this.f$0 = inviteLinkBottomSheet;
        this.f$1 = tLRPC$User;
        this.f$2 = baseFragment;
    }

    public final void run() {
        this.f$0.lambda$new$0(this.f$1, this.f$2);
    }
}
