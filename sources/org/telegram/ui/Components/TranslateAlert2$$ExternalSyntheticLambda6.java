package org.telegram.ui.Components;

import org.telegram.ui.Components.TranslateAlert2;

public final /* synthetic */ class TranslateAlert2$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ TranslateAlert2.OnTranslationSuccess f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ TranslateAlert2$$ExternalSyntheticLambda6(TranslateAlert2.OnTranslationSuccess onTranslationSuccess, String str, String str2) {
        this.f$0 = onTranslationSuccess;
        this.f$1 = str;
        this.f$2 = str2;
    }

    public final void run() {
        TranslateAlert2.lambda$fetchTranslation$6(this.f$0, this.f$1, this.f$2);
    }
}
