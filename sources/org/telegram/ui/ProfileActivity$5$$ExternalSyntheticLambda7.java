package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ProfileActivity;

public final /* synthetic */ class ProfileActivity$5$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ ProfileActivity.AnonymousClass5 f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ TLRPC.TL_chatAdminRights f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ DialogsActivity f$5;

    public /* synthetic */ ProfileActivity$5$$ExternalSyntheticLambda7(ProfileActivity.AnonymousClass5 r1, long j, TLRPC.TL_chatAdminRights tL_chatAdminRights, String str, boolean z, DialogsActivity dialogsActivity) {
        this.f$0 = r1;
        this.f$1 = j;
        this.f$2 = tL_chatAdminRights;
        this.f$3 = str;
        this.f$4 = z;
        this.f$5 = dialogsActivity;
    }

    public final void run() {
        this.f$0.m3080lambda$onItemClick$3$orgtelegramuiProfileActivity$5(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}
