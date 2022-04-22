package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda64 implements RequestDelegate {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ BaseFragment f$1;
    public final /* synthetic */ TLRPC$User f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda64(int i, BaseFragment baseFragment, TLRPC$User tLRPC$User, String str) {
        this.f$0 = i;
        this.f$1 = baseFragment;
        this.f$2 = tLRPC$User;
        this.f$3 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda22(tLRPC$TL_error, this.f$0, this.f$1, this.f$2, this.f$3));
    }
}
