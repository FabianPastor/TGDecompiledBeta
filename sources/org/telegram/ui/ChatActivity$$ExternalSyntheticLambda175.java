package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda175 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC.TL_game f$1;
    public final /* synthetic */ MessageObject f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ long f$4;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda175(ChatActivity chatActivity, TLRPC.TL_game tL_game, MessageObject messageObject, String str, long j) {
        this.f$0 = chatActivity;
        this.f$1 = tL_game;
        this.f$2 = messageObject;
        this.f$3 = str;
        this.f$4 = j;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m1822lambda$showOpenGameAlert$207$orgtelegramuiChatActivity(this.f$1, this.f$2, this.f$3, this.f$4, dialogInterface, i);
    }
}
