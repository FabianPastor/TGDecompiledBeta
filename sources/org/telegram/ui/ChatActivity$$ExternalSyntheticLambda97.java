package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.MessageObject;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda97 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ MessageObject f$2;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda97(ChatActivity chatActivity, int i, MessageObject messageObject) {
        this.f$0 = chatActivity;
        this.f$1 = i;
        this.f$2 = messageObject;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m1796lambda$shareMyContact$75$orgtelegramuiChatActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}
