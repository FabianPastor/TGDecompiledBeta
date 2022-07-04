package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda72 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ TLRPC.User f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ TLRPC.TL_contacts_resolvedPeer f$5;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda72(LaunchActivity launchActivity, int i, String str, TLRPC.User user, String str2, TLRPC.TL_contacts_resolvedPeer tL_contacts_resolvedPeer) {
        this.f$0 = launchActivity;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = user;
        this.f$4 = str2;
        this.f$5 = tL_contacts_resolvedPeer;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3668lambda$runLinkRequest$36$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tL_error);
    }
}
