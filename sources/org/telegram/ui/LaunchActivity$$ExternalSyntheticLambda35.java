package org.telegram.ui;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda35 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ String f$10;
    public final /* synthetic */ TLRPC.TL_chatAdminRights f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ int f$5;
    public final /* synthetic */ TLRPC.Chat f$6;
    public final /* synthetic */ DialogsActivity f$7;
    public final /* synthetic */ TLRPC.User f$8;
    public final /* synthetic */ long f$9;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda35(LaunchActivity launchActivity, String str, TLRPC.TL_chatAdminRights tL_chatAdminRights, boolean z, String str2, int i, TLRPC.Chat chat, DialogsActivity dialogsActivity, TLRPC.User user, long j, String str3) {
        this.f$0 = launchActivity;
        this.f$1 = str;
        this.f$2 = tL_chatAdminRights;
        this.f$3 = z;
        this.f$4 = str2;
        this.f$5 = i;
        this.f$6 = chat;
        this.f$7 = dialogsActivity;
        this.f$8 = user;
        this.f$9 = j;
        this.f$10 = str3;
    }

    public final void run() {
        this.f$0.m3671lambda$runLinkRequest$39$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10);
    }
}
