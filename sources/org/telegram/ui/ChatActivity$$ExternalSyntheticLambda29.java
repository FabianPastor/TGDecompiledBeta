package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$TL_game;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda29 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ TLRPC$TL_game f$1;
    public final /* synthetic */ MessageObject f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ long f$4;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda29(ChatActivity chatActivity, TLRPC$TL_game tLRPC$TL_game, MessageObject messageObject, String str, long j) {
        this.f$0 = chatActivity;
        this.f$1 = tLRPC$TL_game;
        this.f$2 = messageObject;
        this.f$3 = str;
        this.f$4 = j;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$showOpenGameAlert$159(this.f$1, this.f$2, this.f$3, this.f$4, dialogInterface, i);
    }
}
