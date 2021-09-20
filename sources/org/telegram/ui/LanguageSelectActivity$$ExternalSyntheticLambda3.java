package org.telegram.ui;

import java.util.Comparator;
import org.telegram.messenger.LocaleController;

public final /* synthetic */ class LanguageSelectActivity$$ExternalSyntheticLambda3 implements Comparator {
    public final /* synthetic */ LocaleController.LocaleInfo f$0;

    public /* synthetic */ LanguageSelectActivity$$ExternalSyntheticLambda3(LocaleController.LocaleInfo localeInfo) {
        this.f$0 = localeInfo;
    }

    public final int compare(Object obj, Object obj2) {
        return LanguageSelectActivity.lambda$fillLanguages$3(this.f$0, (LocaleController.LocaleInfo) obj, (LocaleController.LocaleInfo) obj2);
    }
}
