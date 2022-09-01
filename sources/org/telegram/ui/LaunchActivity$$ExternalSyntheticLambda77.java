package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda77 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ DialogsActivity f$2;
    public final /* synthetic */ BaseFragment f$3;
    public final /* synthetic */ TLRPC$User f$4;
    public final /* synthetic */ String f$5;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda77(LaunchActivity launchActivity, int i, DialogsActivity dialogsActivity, BaseFragment baseFragment, TLRPC$User tLRPC$User, String str) {
        this.f$0 = launchActivity;
        this.f$1 = i;
        this.f$2 = dialogsActivity;
        this.f$3 = baseFragment;
        this.f$4 = tLRPC$User;
        this.f$5 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$runLinkRequest$36(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tLRPC$TL_error);
    }
}
