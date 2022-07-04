package org.telegram.ui;

import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.ui.ProfileActivity;

public final /* synthetic */ class ProfileActivity$5$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ ProfileActivity.AnonymousClass5 f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC$TL_chatAdminRights f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ DialogsActivity f$5;

    public /* synthetic */ ProfileActivity$5$$ExternalSyntheticLambda5(ProfileActivity.AnonymousClass5 r1, long j, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, String str, boolean z, DialogsActivity dialogsActivity) {
        this.f$0 = r1;
        this.f$1 = j;
        this.f$2 = tLRPC$TL_chatAdminRights;
        this.f$3 = str;
        this.f$4 = z;
        this.f$5 = dialogsActivity;
    }

    public final void run() {
        this.f$0.lambda$onItemClick$3(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
