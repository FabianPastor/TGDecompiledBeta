package org.telegram.messenger;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.ui.ChatActivity;

public final /* synthetic */ class SendMessagesHelper$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ SendMessagesHelper f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ MessageObject f$2;
    public final /* synthetic */ TLRPC$KeyboardButton f$3;
    public final /* synthetic */ ChatActivity f$4;

    public /* synthetic */ SendMessagesHelper$$ExternalSyntheticLambda0(SendMessagesHelper sendMessagesHelper, boolean z, MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton, ChatActivity chatActivity) {
        this.f$0 = sendMessagesHelper;
        this.f$1 = z;
        this.f$2 = messageObject;
        this.f$3 = tLRPC$KeyboardButton;
        this.f$4 = chatActivity;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$sendCallback$25(this.f$1, this.f$2, this.f$3, this.f$4, dialogInterface, i);
    }
}
