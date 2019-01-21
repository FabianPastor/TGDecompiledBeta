package org.telegram.ui.Components;

import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.ui.Components.BotKeyboardView.BotKeyboardViewDelegate;

final /* synthetic */ class ChatActivityEnterView$$Lambda$15 implements BotKeyboardViewDelegate {
    private final ChatActivityEnterView arg$1;

    ChatActivityEnterView$$Lambda$15(ChatActivityEnterView chatActivityEnterView) {
        this.arg$1 = chatActivityEnterView;
    }

    public void didPressedButton(KeyboardButton keyboardButton) {
        this.arg$1.lambda$setButtons$15$ChatActivityEnterView(keyboardButton);
    }
}
