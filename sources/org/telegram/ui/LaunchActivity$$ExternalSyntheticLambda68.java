package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda68 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ TLRPC$User f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ TLRPC$TL_contacts_resolvedPeer f$4;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda68(LaunchActivity launchActivity, int i, TLRPC$User tLRPC$User, String str, TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer) {
        this.f$0 = launchActivity;
        this.f$1 = i;
        this.f$2 = tLRPC$User;
        this.f$3 = str;
        this.f$4 = tLRPC$TL_contacts_resolvedPeer;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$runLinkRequest$32(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
    }
}
