package org.telegram.ui;

import java.util.Comparator;
import org.telegram.messenger.LocaleController.LocaleInfo;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LanguageSelectActivity$bKvkTCAKOIGl0v4A4OGKsEAtQUU implements Comparator {
    private final /* synthetic */ LocaleInfo f$0;

    public /* synthetic */ -$$Lambda$LanguageSelectActivity$bKvkTCAKOIGl0v4A4OGKsEAtQUU(LocaleInfo localeInfo) {
        this.f$0 = localeInfo;
    }

    public final int compare(Object obj, Object obj2) {
        return LanguageSelectActivity.lambda$fillLanguages$3(this.f$0, (LocaleInfo) obj, (LocaleInfo) obj2);
    }
}
