package org.telegram.ui;

import java.util.Comparator;
import org.telegram.messenger.LocaleController;

public final /* synthetic */ class RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda2 implements Comparator {
    public final /* synthetic */ LocaleController.LocaleInfo f$0;

    public /* synthetic */ RestrictedLanguagesSelectActivity$$ExternalSyntheticLambda2(LocaleController.LocaleInfo localeInfo) {
        this.f$0 = localeInfo;
    }

    public final int compare(Object obj, Object obj2) {
        return RestrictedLanguagesSelectActivity.lambda$fillLanguages$4(this.f$0, (LocaleController.LocaleInfo) obj, (LocaleController.LocaleInfo) obj2);
    }
}
