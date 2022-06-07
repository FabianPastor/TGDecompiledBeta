package org.telegram.ui.Components;

import org.telegram.ui.Components.TranslateAlert;

public final /* synthetic */ class TranslateAlert$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ TranslateAlert.OnTranslationSuccess f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ TranslateAlert$$ExternalSyntheticLambda6(TranslateAlert.OnTranslationSuccess onTranslationSuccess, String str, String str2) {
        this.f$0 = onTranslationSuccess;
        this.f$1 = str;
        this.f$2 = str2;
    }

    public final void run() {
        TranslateAlert.lambda$fetchTranslation$9(this.f$0, this.f$1, this.f$2);
    }
}
