package org.telegram.ui.Components;

import android.content.DialogInterface;
import java.util.concurrent.atomic.AtomicBoolean;
import org.telegram.ui.Components.BotWebViewContainer;

public final /* synthetic */ class BotWebViewContainer$$ExternalSyntheticLambda2 implements DialogInterface.OnClickListener {
    public final /* synthetic */ BotWebViewContainer f$0;
    public final /* synthetic */ BotWebViewContainer.PopupButton f$1;
    public final /* synthetic */ AtomicBoolean f$2;

    public /* synthetic */ BotWebViewContainer$$ExternalSyntheticLambda2(BotWebViewContainer botWebViewContainer, BotWebViewContainer.PopupButton popupButton, AtomicBoolean atomicBoolean) {
        this.f$0 = botWebViewContainer;
        this.f$1 = popupButton;
        this.f$2 = atomicBoolean;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onEventReceived$7(this.f$1, this.f$2, dialogInterface, i);
    }
}
