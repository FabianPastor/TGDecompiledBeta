package org.telegram.p005ui.Components;

import org.telegram.p005ui.Components.BotKeyboardView.BotKeyboardViewDelegate;
import org.telegram.tgnet.TLRPC.KeyboardButton;

/* renamed from: org.telegram.ui.Components.ChatActivityEnterView$$Lambda$15 */
final /* synthetic */ class ChatActivityEnterView$$Lambda$15 implements BotKeyboardViewDelegate {
    private final ChatActivityEnterView arg$1;

    ChatActivityEnterView$$Lambda$15(ChatActivityEnterView chatActivityEnterView) {
        this.arg$1 = chatActivityEnterView;
    }

    public void didPressedButton(KeyboardButton keyboardButton) {
        this.arg$1.lambda$setButtons$15$ChatActivityEnterView(keyboardButton);
    }
}
