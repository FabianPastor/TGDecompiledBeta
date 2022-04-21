package org.telegram.ui;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda60 implements RequestDelegate {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ BaseFragment f$1;
    public final /* synthetic */ TLRPC.User f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda60(int i, BaseFragment baseFragment, TLRPC.User user, String str) {
        this.f$0 = i;
        this.f$1 = baseFragment;
        this.f$2 = user;
        this.f$3 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new LaunchActivity$$ExternalSyntheticLambda14(tL_error, this.f$0, this.f$1, this.f$2, this.f$3));
    }
}
