package org.telegram.ui;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda58 implements MessagesController.IsInChatCheckedCallback {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ TLRPC.User f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ DialogsActivity f$4;
    public final /* synthetic */ int f$5;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda58(LaunchActivity launchActivity, String str, TLRPC.User user, long j, DialogsActivity dialogsActivity, int i) {
        this.f$0 = launchActivity;
        this.f$1 = str;
        this.f$2 = user;
        this.f$3 = j;
        this.f$4 = dialogsActivity;
        this.f$5 = i;
    }

    public final void run(boolean z, TLRPC.TL_chatAdminRights tL_chatAdminRights, String str) {
        this.f$0.m2349lambda$runLinkRequest$35$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, z, tL_chatAdminRights, str);
    }
}
