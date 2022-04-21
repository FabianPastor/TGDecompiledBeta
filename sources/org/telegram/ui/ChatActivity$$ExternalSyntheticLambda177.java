package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda177 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC.User f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda177(ChatActivity chatActivity, TLRPC.User user) {
        this.f$0 = chatActivity;
        this.f$1 = user;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m1755lambda$onTransitionAnimationEnd$130$orgtelegramuiChatActivity(this.f$1, dialogInterface, i);
    }
}
