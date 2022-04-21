package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda64 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC.User f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ TLRPC.TL_contacts_resolvedPeer f$4;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda64(LaunchActivity launchActivity, int i, TLRPC.User user, String str, TLRPC.TL_contacts_resolvedPeer tL_contacts_resolvedPeer) {
        this.f$0 = launchActivity;
        this.f$1 = i;
        this.f$2 = user;
        this.f$3 = str;
        this.f$4 = tL_contacts_resolvedPeer;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2346lambda$runLinkRequest$32$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
