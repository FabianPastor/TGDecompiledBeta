package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda22 implements Runnable {
    public final /* synthetic */ TLRPC$TL_error f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ BaseFragment f$2;
    public final /* synthetic */ TLRPC$User f$3;
    public final /* synthetic */ String f$4;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda22(TLRPC$TL_error tLRPC$TL_error, int i, BaseFragment baseFragment, TLRPC$User tLRPC$User, String str) {
        this.f$0 = tLRPC$TL_error;
        this.f$1 = i;
        this.f$2 = baseFragment;
        this.f$3 = tLRPC$User;
        this.f$4 = str;
    }

    public final void run() {
        LaunchActivity.lambda$runLinkRequest$28(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
