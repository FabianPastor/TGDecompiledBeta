package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda76 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ DialogsActivity f$2;
    public final /* synthetic */ BaseFragment f$3;
    public final /* synthetic */ TLRPC.User f$4;
    public final /* synthetic */ String f$5;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda76(LaunchActivity launchActivity, int i, DialogsActivity dialogsActivity, BaseFragment baseFragment, TLRPC.User user, String str) {
        this.f$0 = launchActivity;
        this.f$1 = i;
        this.f$2 = dialogsActivity;
        this.f$3 = baseFragment;
        this.f$4 = user;
        this.f$5 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3665lambda$runLinkRequest$33$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tL_error);
    }
}
