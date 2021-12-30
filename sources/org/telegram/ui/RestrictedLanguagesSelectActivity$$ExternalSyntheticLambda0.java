package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.LocaleController;

public final /* synthetic */ class RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ RestrictedLanguagesSelectActivity f$0;
    public final /* synthetic */ LocaleController.LocaleInfo f$1;

    public /* synthetic */ RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda0(RestrictedLanguagesSelectActivity restrictedLanguagesSelectActivity, LocaleController.LocaleInfo localeInfo) {
        this.f$0 = restrictedLanguagesSelectActivity;
        this.f$1 = localeInfo;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$createView$2(this.f$1, dialogInterface, i);
    }
}
