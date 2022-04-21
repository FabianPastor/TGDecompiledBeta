package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda34 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ TLRPC.User f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ TLRPC.TL_contacts_resolvedPeer f$5;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda34(LaunchActivity launchActivity, TLObject tLObject, int i, TLRPC.User user, String str, TLRPC.TL_contacts_resolvedPeer tL_contacts_resolvedPeer) {
        this.f$0 = launchActivity;
        this.f$1 = tLObject;
        this.f$2 = i;
        this.f$3 = user;
        this.f$4 = str;
        this.f$5 = tL_contacts_resolvedPeer;
    }

    public final void run() {
        this.f$0.m2345lambda$runLinkRequest$31$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
