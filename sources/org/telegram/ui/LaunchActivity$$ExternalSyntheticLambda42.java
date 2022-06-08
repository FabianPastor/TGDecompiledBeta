package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda42 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ TLRPC$User f$4;
    public final /* synthetic */ String f$5;
    public final /* synthetic */ TLRPC$TL_contacts_resolvedPeer f$6;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda42(LaunchActivity launchActivity, TLObject tLObject, int i, String str, TLRPC$User tLRPC$User, String str2, TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer) {
        this.f$0 = launchActivity;
        this.f$1 = tLObject;
        this.f$2 = i;
        this.f$3 = str;
        this.f$4 = tLRPC$User;
        this.f$5 = str2;
        this.f$6 = tLRPC$TL_contacts_resolvedPeer;
    }

    public final void run() {
        this.f$0.lambda$runLinkRequest$35(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
