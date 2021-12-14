package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ChatThemeBottomSheet;

public final /* synthetic */ class ChatThemeBottomSheet$Adapter$$ExternalSyntheticLambda1 implements RequestDelegate {
    public final /* synthetic */ ChatThemeBottomSheet.Adapter f$0;
    public final /* synthetic */ Theme.ThemeInfo f$1;

    public /* synthetic */ ChatThemeBottomSheet$Adapter$$ExternalSyntheticLambda1(ChatThemeBottomSheet.Adapter adapter, Theme.ThemeInfo themeInfo) {
        this.f$0 = adapter;
        this.f$1 = themeInfo;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$parseTheme$1(this.f$1, tLObject, tLRPC$TL_error);
    }
}
