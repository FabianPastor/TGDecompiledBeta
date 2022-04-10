package org.telegram.ui.Components;

import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.ui.Components.BotKeyboardView;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda56 implements BotKeyboardView.BotKeyboardViewDelegate {
    public final /* synthetic */ ChatActivityEnterView f$0;

    public /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda56(ChatActivityEnterView chatActivityEnterView) {
        this.f$0 = chatActivityEnterView;
    }

    public final void didPressedButton(TLRPC$KeyboardButton tLRPC$KeyboardButton) {
        this.f$0.lambda$setButtons$45(tLRPC$KeyboardButton);
    }
}
