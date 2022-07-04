package org.telegram.ui.Components;

import org.telegram.ui.Components.TranslateAlert;

public final /* synthetic */ class TranslateAlert$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ TranslateAlert.OnTranslationFail f$0;

    public /* synthetic */ TranslateAlert$$ExternalSyntheticLambda9(TranslateAlert.OnTranslationFail onTranslationFail) {
        this.f$0 = onTranslationFail;
    }

    public final void run() {
        this.f$0.run(false);
    }
}
