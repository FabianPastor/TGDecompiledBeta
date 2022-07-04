package org.telegram.ui.Components;

import org.telegram.ui.Components.TranslateAlert;

public final /* synthetic */ class TranslateAlert$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ TranslateAlert f$0;
    public final /* synthetic */ CharSequence f$1;
    public final /* synthetic */ TranslateAlert.OnTranslationSuccess f$2;
    public final /* synthetic */ long f$3;
    public final /* synthetic */ TranslateAlert.OnTranslationFail f$4;

    public /* synthetic */ TranslateAlert$$ExternalSyntheticLambda2(TranslateAlert translateAlert, CharSequence charSequence, TranslateAlert.OnTranslationSuccess onTranslationSuccess, long j, TranslateAlert.OnTranslationFail onTranslationFail) {
        this.f$0 = translateAlert;
        this.f$1 = charSequence;
        this.f$2 = onTranslationSuccess;
        this.f$3 = j;
        this.f$4 = onTranslationFail;
    }

    public final void run() {
        this.f$0.m1508x8a9var_c(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
