package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$EvCHw7kw93yB8Oxz0Ey3T0TDVYc implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ ThemeInfo f$1;

    public /* synthetic */ -$$Lambda$MessagesController$EvCHw7kw93yB8Oxz0Ey3T0TDVYc(MessagesController messagesController, ThemeInfo themeInfo) {
        this.f$0 = messagesController;
        this.f$1 = themeInfo;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$didReceivedNotification$16$MessagesController(this.f$1, tLObject, tL_error);
    }
}
