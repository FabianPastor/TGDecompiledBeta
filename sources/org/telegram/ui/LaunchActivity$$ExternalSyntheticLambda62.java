package org.telegram.ui;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda62 implements MessagesController.IsInChatCheckedCallback {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ TLRPC$User f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ DialogsActivity f$4;
    public final /* synthetic */ int f$5;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda62(LaunchActivity launchActivity, String str, TLRPC$User tLRPC$User, long j, DialogsActivity dialogsActivity, int i) {
        this.f$0 = launchActivity;
        this.f$1 = str;
        this.f$2 = tLRPC$User;
        this.f$3 = j;
        this.f$4 = dialogsActivity;
        this.f$5 = i;
    }

    public final void run(boolean z, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, String str) {
        this.f$0.lambda$runLinkRequest$35(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, z, tLRPC$TL_chatAdminRights, str);
    }
}
