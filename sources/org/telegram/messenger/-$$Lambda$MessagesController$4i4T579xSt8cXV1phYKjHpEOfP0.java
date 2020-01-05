package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.Theme.ThemeAccent;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$4i4T579xSt8cXV1phYKjHpEOfP0 implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ ThemeInfo f$1;
    private final /* synthetic */ ThemeAccent f$2;

    public /* synthetic */ -$$Lambda$MessagesController$4i4T579xSt8cXV1phYKjHpEOfP0(MessagesController messagesController, ThemeInfo themeInfo, ThemeAccent themeAccent) {
        this.f$0 = messagesController;
        this.f$1 = themeInfo;
        this.f$2 = themeAccent;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$null$14$MessagesController(this.f$1, this.f$2, tLObject, tL_error);
    }
}
