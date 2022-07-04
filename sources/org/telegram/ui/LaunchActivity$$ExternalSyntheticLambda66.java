package org.telegram.ui;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda66 implements MessagesController.IsInChatCheckedCallback {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ TLRPC.Chat f$4;
    public final /* synthetic */ DialogsActivity f$5;
    public final /* synthetic */ TLRPC.User f$6;
    public final /* synthetic */ long f$7;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda66(LaunchActivity launchActivity, String str, String str2, int i, TLRPC.Chat chat, DialogsActivity dialogsActivity, TLRPC.User user, long j) {
        this.f$0 = launchActivity;
        this.f$1 = str;
        this.f$2 = str2;
        this.f$3 = i;
        this.f$4 = chat;
        this.f$5 = dialogsActivity;
        this.f$6 = user;
        this.f$7 = j;
    }

    public final void run(boolean z, TLRPC.TL_chatAdminRights tL_chatAdminRights, String str) {
        this.f$0.m3672lambda$runLinkRequest$40$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, z, tL_chatAdminRights, str);
    }
}
