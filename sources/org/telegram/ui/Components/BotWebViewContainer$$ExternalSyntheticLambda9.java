package org.telegram.ui.Components;

import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;

public final /* synthetic */ class BotWebViewContainer$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ BotWebViewContainer f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ ActionBarMenuSubItem f$2;

    public /* synthetic */ BotWebViewContainer$$ExternalSyntheticLambda9(BotWebViewContainer botWebViewContainer, TLObject tLObject, ActionBarMenuSubItem actionBarMenuSubItem) {
        this.f$0 = botWebViewContainer;
        this.f$1 = tLObject;
        this.f$2 = actionBarMenuSubItem;
    }

    public final void run() {
        this.f$0.lambda$loadFlickerAndSettingsItem$4(this.f$1, this.f$2);
    }
}
