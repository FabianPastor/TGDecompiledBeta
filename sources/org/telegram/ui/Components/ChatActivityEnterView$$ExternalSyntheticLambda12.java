package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$KeyboardButton;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda12 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatActivityEnterView f$0;
    public final /* synthetic */ MessageObject f$1;
    public final /* synthetic */ TLRPC$KeyboardButton f$2;

    public /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda12(ChatActivityEnterView chatActivityEnterView, MessageObject messageObject, TLRPC$KeyboardButton tLRPC$KeyboardButton) {
        this.f$0 = chatActivityEnterView;
        this.f$1 = messageObject;
        this.f$2 = tLRPC$KeyboardButton;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$didPressedBotButton$44(this.f$1, this.f$2, dialogInterface, i);
    }
}
