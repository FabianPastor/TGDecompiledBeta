package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda41 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ DialogsActivity f$3;
    public final /* synthetic */ BaseFragment f$4;
    public final /* synthetic */ TLRPC.User f$5;
    public final /* synthetic */ String f$6;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda41(LaunchActivity launchActivity, TLObject tLObject, int i, DialogsActivity dialogsActivity, BaseFragment baseFragment, TLRPC.User user, String str) {
        this.f$0 = launchActivity;
        this.f$1 = tLObject;
        this.f$2 = i;
        this.f$3 = dialogsActivity;
        this.f$4 = baseFragment;
        this.f$5 = user;
        this.f$6 = str;
    }

    public final void run() {
        this.f$0.m3664lambda$runLinkRequest$32$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
