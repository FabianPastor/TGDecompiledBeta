package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda243 implements RequestDelegate {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ TLRPC.TL_theme f$1;
    public final /* synthetic */ Theme.ThemeInfo f$2;
    public final /* synthetic */ TLRPC.TL_inputThemeSettings f$3;
    public final /* synthetic */ Theme.ThemeAccent f$4;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda243(MessagesController messagesController, TLRPC.TL_theme tL_theme, Theme.ThemeInfo themeInfo, TLRPC.TL_inputThemeSettings tL_inputThemeSettings, Theme.ThemeAccent themeAccent) {
        this.f$0 = messagesController;
        this.f$1 = tL_theme;
        this.f$2 = themeInfo;
        this.f$3 = tL_inputThemeSettings;
        this.f$4 = themeAccent;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m200xf8eCLASSNAME(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
