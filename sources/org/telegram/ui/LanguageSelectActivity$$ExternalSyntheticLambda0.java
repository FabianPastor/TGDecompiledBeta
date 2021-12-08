package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.LocaleController;

public final /* synthetic */ class LanguageSelectActivity$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ LanguageSelectActivity f$0;
    public final /* synthetic */ LocaleController.LocaleInfo f$1;

    public /* synthetic */ LanguageSelectActivity$$ExternalSyntheticLambda0(LanguageSelectActivity languageSelectActivity, LocaleController.LocaleInfo localeInfo) {
        this.f$0 = languageSelectActivity;
        this.f$1 = localeInfo;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3059lambda$createView$1$orgtelegramuiLanguageSelectActivity(this.f$1, dialogInterface, i);
    }
}
