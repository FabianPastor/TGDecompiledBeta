package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;

public final /* synthetic */ class BotWebViewContainer$$ExternalSyntheticLambda12 implements RequestDelegate {
    public final /* synthetic */ BotWebViewContainer f$0;
    public final /* synthetic */ ActionBarMenuSubItem f$1;

    public /* synthetic */ BotWebViewContainer$$ExternalSyntheticLambda12(BotWebViewContainer botWebViewContainer, ActionBarMenuSubItem actionBarMenuSubItem) {
        this.f$0 = botWebViewContainer;
        this.f$1 = actionBarMenuSubItem;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadFlickerAndSettingsItem$5(this.f$1, tLObject, tLRPC$TL_error);
    }
}
