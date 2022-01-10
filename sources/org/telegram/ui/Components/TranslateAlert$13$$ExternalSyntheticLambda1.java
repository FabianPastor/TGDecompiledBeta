package org.telegram.ui.Components;

import org.telegram.ui.Components.TranslateAlert;

public final /* synthetic */ class TranslateAlert$13$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ TranslateAlert.OnTranslationFail f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ TranslateAlert$13$$ExternalSyntheticLambda1(TranslateAlert.OnTranslationFail onTranslationFail, boolean z) {
        this.f$0 = onTranslationFail;
        this.f$1 = z;
    }

    public final void run() {
        this.f$0.run(this.f$1);
    }
}
