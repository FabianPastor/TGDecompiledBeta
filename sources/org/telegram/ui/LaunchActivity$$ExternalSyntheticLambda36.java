package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda36 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ TLRPC$TL_chatAdminRights f$2;
    public final /* synthetic */ TLRPC$User f$3;
    public final /* synthetic */ long f$4;
    public final /* synthetic */ String f$5;
    public final /* synthetic */ boolean f$6;
    public final /* synthetic */ DialogsActivity f$7;
    public final /* synthetic */ int f$8;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda36(LaunchActivity launchActivity, String str, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, TLRPC$User tLRPC$User, long j, String str2, boolean z, DialogsActivity dialogsActivity, int i) {
        this.f$0 = launchActivity;
        this.f$1 = str;
        this.f$2 = tLRPC$TL_chatAdminRights;
        this.f$3 = tLRPC$User;
        this.f$4 = j;
        this.f$5 = str2;
        this.f$6 = z;
        this.f$7 = dialogsActivity;
        this.f$8 = i;
    }

    public final void run() {
        this.f$0.lambda$runLinkRequest$34(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
