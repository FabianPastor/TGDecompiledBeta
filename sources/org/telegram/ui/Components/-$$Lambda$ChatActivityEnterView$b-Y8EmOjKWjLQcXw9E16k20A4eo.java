package org.telegram.ui.Components;

import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.ui.Components.BotKeyboardView.BotKeyboardViewDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivityEnterView$b-Y8EmOjKWjLQcXw9E16k20A4eo implements BotKeyboardViewDelegate {
    private final /* synthetic */ ChatActivityEnterView f$0;

    public /* synthetic */ -$$Lambda$ChatActivityEnterView$b-Y8EmOjKWjLQcXw9E16k20A4eo(ChatActivityEnterView chatActivityEnterView) {
        this.f$0 = chatActivityEnterView;
    }

    public final void didPressedButton(KeyboardButton keyboardButton) {
        this.f$0.lambda$setButtons$15$ChatActivityEnterView(keyboardButton);
    }
}
