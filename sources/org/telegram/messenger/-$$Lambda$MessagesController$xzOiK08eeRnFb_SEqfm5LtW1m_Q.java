package org.telegram.messenger;

import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme.ThemeAccent;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$xzOiK08eeRnFb_SEqfm5LtW1m_Q implements Runnable {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TLObject f$1;
    private final /* synthetic */ ThemeInfo f$2;
    private final /* synthetic */ ThemeAccent f$3;

    public /* synthetic */ -$$Lambda$MessagesController$xzOiK08eeRnFb_SEqfm5LtW1m_Q(MessagesController messagesController, TLObject tLObject, ThemeInfo themeInfo, ThemeAccent themeAccent) {
        this.f$0 = messagesController;
        this.f$1 = tLObject;
        this.f$2 = themeInfo;
        this.f$3 = themeAccent;
    }

    public final void run() {
        this.f$0.lambda$null$11$MessagesController(this.f$1, this.f$2, this.f$3);
    }
}
