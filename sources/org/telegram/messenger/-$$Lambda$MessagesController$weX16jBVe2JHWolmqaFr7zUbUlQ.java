package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$weX16jBVe2JHWolmqaFr7zUbUlQ implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ ThemeInfo f$1;

    public /* synthetic */ -$$Lambda$MessagesController$weX16jBVe2JHWolmqaFr7zUbUlQ(MessagesController messagesController, ThemeInfo themeInfo) {
        this.f$0 = messagesController;
        this.f$1 = themeInfo;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$12$MessagesController(this.f$1, tLObject, tL_error);
    }
}
