package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda194 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC.User f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda194(ChatActivity chatActivity, TLRPC.User user) {
        this.f$0 = chatActivity;
        this.f$1 = user;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3036lambda$onTransitionAnimationEnd$133$orgtelegramuiChatActivity(this.f$1, dialogInterface, i);
    }
}
