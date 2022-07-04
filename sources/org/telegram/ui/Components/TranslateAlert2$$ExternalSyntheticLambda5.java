package org.telegram.ui.Components;

import org.telegram.ui.Components.TranslateAlert2;

public final /* synthetic */ class TranslateAlert2$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ TranslateAlert2.OnTranslationFail f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ TranslateAlert2$$ExternalSyntheticLambda5(TranslateAlert2.OnTranslationFail onTranslationFail, boolean z) {
        this.f$0 = onTranslationFail;
        this.f$1 = z;
    }

    public final void run() {
        this.f$0.run(this.f$1);
    }
}
