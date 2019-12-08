package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivityEnterView$o4Mi39Pyn81ZsI7SIijOXifQPj8 implements DialogsActivityDelegate {
    private final /* synthetic */ ChatActivityEnterView f$0;
    private final /* synthetic */ MessageObject f$1;
    private final /* synthetic */ KeyboardButton f$2;

    public /* synthetic */ -$$Lambda$ChatActivityEnterView$o4Mi39Pyn81ZsI7SIijOXifQPj8(ChatActivityEnterView chatActivityEnterView, MessageObject messageObject, KeyboardButton keyboardButton) {
        this.f$0 = chatActivityEnterView;
        this.f$1 = messageObject;
        this.f$2 = keyboardButton;
    }

    public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.f$0.lambda$didPressedBotButton$26$ChatActivityEnterView(this.f$1, this.f$2, dialogsActivity, arrayList, charSequence, z);
    }
}
