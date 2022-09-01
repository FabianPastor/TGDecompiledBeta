package org.telegram.ui;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda68 implements MessagesController.IsInChatCheckedCallback {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ TLRPC$Chat f$4;
    public final /* synthetic */ DialogsActivity f$5;
    public final /* synthetic */ TLRPC$User f$6;
    public final /* synthetic */ long f$7;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda68(LaunchActivity launchActivity, String str, String str2, int i, TLRPC$Chat tLRPC$Chat, DialogsActivity dialogsActivity, TLRPC$User tLRPC$User, long j) {
        this.f$0 = launchActivity;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = i;
        this.f$4 = tLRPC$Chat;
        this.f$5 = dialogsActivity;
        this.f$6 = tLRPC$User;
        this.f$7 = j;
    }

    public final void run(boolean z, TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights, String str) {
        this.f$0.lambda$runLinkRequest$43(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, z, tLRPC$TL_chatAdminRights, str);
    }
}
