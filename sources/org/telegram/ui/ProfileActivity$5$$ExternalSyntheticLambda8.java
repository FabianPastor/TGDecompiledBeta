package org.telegram.ui;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.ui.ProfileActivity;

public final /* synthetic */ class ProfileActivity$5$$ExternalSyntheticLambda8 implements MessagesController.IsInChatCheckedCallback {
    public final /* synthetic */ ProfileActivity.AnonymousClass5 f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ DialogsActivity f$2;

    public /* synthetic */ ProfileActivity$5$$ExternalSyntheticLambda8(ProfileActivity.AnonymousClass5 r1, long j, DialogsActivity dialogsActivity) {
        this.f$0 = r1;
        this.f$1 = j;
        this.f$2 = dialogsActivity;
    }

    public final void run(boolean z, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, String str) {
        this.f$0.lambda$onItemClick$4(this.f$1, this.f$2, z, tLRPC$TL_chatAdminRights, str);
    }
}
