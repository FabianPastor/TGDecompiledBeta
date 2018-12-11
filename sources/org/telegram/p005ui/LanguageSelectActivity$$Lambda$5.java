package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.LocaleController.LocaleInfo;

/* renamed from: org.telegram.ui.LanguageSelectActivity$$Lambda$5 */
final /* synthetic */ class LanguageSelectActivity$$Lambda$5 implements OnClickListener {
    private final LanguageSelectActivity arg$1;
    private final LocaleInfo arg$2;

    LanguageSelectActivity$$Lambda$5(LanguageSelectActivity languageSelectActivity, LocaleInfo localeInfo) {
        this.arg$1 = languageSelectActivity;
        this.arg$2 = localeInfo;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$1$LanguageSelectActivity(this.arg$2, dialogInterface, i);
    }
}
