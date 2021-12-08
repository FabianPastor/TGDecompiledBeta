package org.telegram.ui;

import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda27 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ Integer f$3;
    public final /* synthetic */ TLRPC.Chat f$4;
    public final /* synthetic */ TLRPC.TL_messages_getDiscussionMessage f$5;
    public final /* synthetic */ Integer f$6;
    public final /* synthetic */ Integer f$7;
    public final /* synthetic */ AlertDialog f$8;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda27(LaunchActivity launchActivity, TLObject tLObject, int i, Integer num, TLRPC.Chat chat, TLRPC.TL_messages_getDiscussionMessage tL_messages_getDiscussionMessage, Integer num2, Integer num3, AlertDialog alertDialog) {
        this.f$0 = launchActivity;
        this.f$1 = tLObject;
        this.f$2 = i;
        this.f$3 = num;
        this.f$4 = chat;
        this.f$5 = tL_messages_getDiscussionMessage;
        this.f$6 = num2;
        this.f$7 = num3;
        this.f$8 = alertDialog;
    }

    public final void run() {
        this.f$0.m3087lambda$runCommentRequest$19$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}
