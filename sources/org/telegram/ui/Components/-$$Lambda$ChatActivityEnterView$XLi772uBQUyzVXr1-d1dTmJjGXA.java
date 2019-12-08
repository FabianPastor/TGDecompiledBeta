package org.telegram.ui.Components;

import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.ui.Components.BotKeyboardView.BotKeyboardViewDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivityEnterView$XLi772uBQUyzVXr1-d1dTmJjGXA implements BotKeyboardViewDelegate {
    private final /* synthetic */ ChatActivityEnterView f$0;

    public /* synthetic */ -$$Lambda$ChatActivityEnterView$XLi772uBQUyzVXr1-d1dTmJjGXA(ChatActivityEnterView chatActivityEnterView) {
        this.f$0 = chatActivityEnterView;
    }

    public final void didPressedButton(KeyboardButton keyboardButton) {
        this.f$0.lambda$setButtons$19$ChatActivityEnterView(keyboardButton);
    }
}
