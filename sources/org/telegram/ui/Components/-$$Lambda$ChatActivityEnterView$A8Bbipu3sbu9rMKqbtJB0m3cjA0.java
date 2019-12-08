package org.telegram.ui.Components;

import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.ui.Components.BotKeyboardView.BotKeyboardViewDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivityEnterView$A8Bbipu3sbu9rMKqbtJB0m3cjA0 implements BotKeyboardViewDelegate {
    private final /* synthetic */ ChatActivityEnterView f$0;

    public /* synthetic */ -$$Lambda$ChatActivityEnterView$A8Bbipu3sbu9rMKqbtJB0m3cjA0(ChatActivityEnterView chatActivityEnterView) {
        this.f$0 = chatActivityEnterView;
    }

    public final void didPressedButton(KeyboardButton keyboardButton) {
        this.f$0.lambda$setButtons$24$ChatActivityEnterView(keyboardButton);
    }
}
