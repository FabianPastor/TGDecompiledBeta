package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.LocaleController.LocaleInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LanguageSelectActivity$oSx7KAjKIG5eHsslTckzl6eK4-U implements OnClickListener {
    private final /* synthetic */ LanguageSelectActivity f$0;
    private final /* synthetic */ LocaleInfo f$1;

    public /* synthetic */ -$$Lambda$LanguageSelectActivity$oSx7KAjKIG5eHsslTckzl6eK4-U(LanguageSelectActivity languageSelectActivity, LocaleInfo localeInfo) {
        this.f$0 = languageSelectActivity;
        this.f$1 = localeInfo;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$1$LanguageSelectActivity(this.f$1, dialogInterface, i);
    }
}
