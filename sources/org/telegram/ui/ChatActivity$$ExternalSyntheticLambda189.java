package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.MessageObject;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda189 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ MessageObject f$1;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda189(ChatActivity chatActivity, MessageObject messageObject) {
        this.f$0 = chatActivity;
        this.f$1 = messageObject;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3075lambda$processSelectedOption$212$orgtelegramuiChatActivity(this.f$1, dialogInterface, i);
    }
}
