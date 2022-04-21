package org.telegram.ui;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ProfileActivity;

public final /* synthetic */ class ProfileActivity$5$$ExternalSyntheticLambda10 implements MessagesController.IsInChatCheckedCallback {
    public final /* synthetic */ ProfileActivity.AnonymousClass5 f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ DialogsActivity f$2;

    public /* synthetic */ ProfileActivity$5$$ExternalSyntheticLambda10(ProfileActivity.AnonymousClass5 r1, long j, DialogsActivity dialogsActivity) {
        this.f$0 = r1;
        this.f$1 = j;
        this.f$2 = dialogsActivity;
    }

    public final void run(boolean z, TLRPC.TL_chatAdminRights tL_chatAdminRights, String str) {
        this.f$0.m3081lambda$onItemClick$4$orgtelegramuiProfileActivity$5(this.f$1, this.f$2, z, tL_chatAdminRights, str);
    }
}
