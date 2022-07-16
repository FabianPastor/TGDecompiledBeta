package org.telegram.ui.Components;

import android.content.DialogInterface;
import java.util.concurrent.atomic.AtomicBoolean;

public final /* synthetic */ class BotWebViewContainer$$ExternalSyntheticLambda5 implements DialogInterface.OnDismissListener {
    public final /* synthetic */ BotWebViewContainer f$0;
    public final /* synthetic */ AtomicBoolean f$1;

    public /* synthetic */ BotWebViewContainer$$ExternalSyntheticLambda5(BotWebViewContainer botWebViewContainer, AtomicBoolean atomicBoolean) {
        this.f$0 = botWebViewContainer;
        this.f$1 = atomicBoolean;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        this.f$0.lambda$onEventReceived$10(this.f$1, dialogInterface);
    }
}
