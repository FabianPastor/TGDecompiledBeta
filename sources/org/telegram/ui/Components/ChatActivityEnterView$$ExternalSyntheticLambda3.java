package org.telegram.ui.Components;

import android.content.DialogInterface;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda3 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatActivityEnterView f$0;
    public final /* synthetic */ MessageObject f$1;
    public final /* synthetic */ TLRPC.KeyboardButton f$2;

    public /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda3(ChatActivityEnterView chatActivityEnterView, MessageObject messageObject, TLRPC.KeyboardButton keyboardButton) {
        this.f$0 = chatActivityEnterView;
        this.f$1 = messageObject;
        this.f$2 = keyboardButton;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m2038x5ade236f(this.f$1, this.f$2, dialogInterface, i);
    }
}
