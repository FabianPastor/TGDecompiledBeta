package org.telegram.ui;

import org.telegram.ui.CountrySelectActivity.CountrySelectActivityDelegate;

final /* synthetic */ class NewContactActivity$$Lambda$9 implements CountrySelectActivityDelegate {
    private final NewContactActivity arg$1;

    NewContactActivity$$Lambda$9(NewContactActivity newContactActivity) {
        this.arg$1 = newContactActivity;
    }

    public void didSelectCountry(String str, String str2) {
        this.arg$1.lambda$null$4$NewContactActivity(str, str2);
    }
}
