package org.telegram.ui;

import java.util.Comparator;
import org.telegram.messenger.LocaleController.LocaleInfo;

final /* synthetic */ class LanguageSelectActivity$$Lambda$2 implements Comparator {
    private final LocaleInfo arg$1;

    LanguageSelectActivity$$Lambda$2(LocaleInfo localeInfo) {
        this.arg$1 = localeInfo;
    }

    public int compare(Object obj, Object obj2) {
        return LanguageSelectActivity.lambda$fillLanguages$3$LanguageSelectActivity(this.arg$1, (LocaleInfo) obj, (LocaleInfo) obj2);
    }
}
