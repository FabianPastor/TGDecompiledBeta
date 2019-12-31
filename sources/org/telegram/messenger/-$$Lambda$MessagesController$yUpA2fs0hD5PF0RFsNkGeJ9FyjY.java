package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputThemeSettings;
import org.telegram.tgnet.TLRPC.TL_theme;
import org.telegram.ui.ActionBar.Theme.ThemeAccent;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$yUpA2fs0hD5PF0RFsNkGeJ9FyjY implements RequestDelegate {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ TL_theme f$1;
    private final /* synthetic */ ThemeInfo f$2;
    private final /* synthetic */ TL_inputThemeSettings f$3;
    private final /* synthetic */ ThemeAccent f$4;

    public /* synthetic */ -$$Lambda$MessagesController$yUpA2fs0hD5PF0RFsNkGeJ9FyjY(MessagesController messagesController, TL_theme tL_theme, ThemeInfo themeInfo, TL_inputThemeSettings tL_inputThemeSettings, ThemeAccent themeAccent) {
        this.f$0 = messagesController;
        this.f$1 = tL_theme;
        this.f$2 = themeInfo;
        this.f$3 = tL_inputThemeSettings;
        this.f$4 = themeAccent;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$didReceivedNotification$16$MessagesController(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
