package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC.KeyboardButton;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivityEnterView$ik-HAI0KrZRQZ0KvOtPB4la3mBI implements OnClickListener {
    private final /* synthetic */ ChatActivityEnterView f$0;
    private final /* synthetic */ MessageObject f$1;
    private final /* synthetic */ KeyboardButton f$2;

    public /* synthetic */ -$$Lambda$ChatActivityEnterView$ik-HAI0KrZRQZ0KvOtPB4la3mBI(ChatActivityEnterView chatActivityEnterView, MessageObject messageObject, KeyboardButton keyboardButton) {
        this.f$0 = chatActivityEnterView;
        this.f$1 = messageObject;
        this.f$2 = keyboardButton;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$didPressedBotButton$20$ChatActivityEnterView(this.f$1, this.f$2, dialogInterface, i);
    }
}