package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda252 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ Theme.ThemeInfo f$1;
    public final /* synthetic */ Theme.ThemeAccent f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda252(MessagesController messagesController, Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent) {
        this.f$0 = messagesController;
        this.f$1 = themeInfo;
        this.f$2 = themeAccent;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m198x2e6994f4(this.f$1, this.f$2, tLObject, tL_error);
    }
}
