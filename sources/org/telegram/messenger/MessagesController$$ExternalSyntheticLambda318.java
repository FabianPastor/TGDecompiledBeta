package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputThemeSettings;
import org.telegram.tgnet.TLRPC$TL_theme;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda318 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC$TL_theme f$1;
    public final /* synthetic */ Theme.ThemeInfo f$2;
    public final /* synthetic */ TLRPC$TL_inputThemeSettings f$3;
    public final /* synthetic */ Theme.ThemeAccent f$4;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda318(MessagesController messagesController, TLRPC$TL_theme tLRPC$TL_theme, Theme.ThemeInfo themeInfo, TLRPC$TL_inputThemeSettings tLRPC$TL_inputThemeSettings, Theme.ThemeAccent themeAccent) {
        this.f$0 = messagesController;
        this.f$1 = tLRPC$TL_theme;
        this.f$2 = themeInfo;
        this.f$3 = tLRPC$TL_inputThemeSettings;
        this.f$4 = themeAccent;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$didReceivedNotification$32(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tLRPC$TL_error);
    }
}
