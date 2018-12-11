package org.telegram.p005ui;

import org.telegram.p005ui.CountrySelectActivity.CountrySelectActivityDelegate;

/* renamed from: org.telegram.ui.NewContactActivity$$Lambda$8 */
final /* synthetic */ class NewContactActivity$$Lambda$8 implements CountrySelectActivityDelegate {
    private final NewContactActivity arg$1;

    NewContactActivity$$Lambda$8(NewContactActivity newContactActivity) {
        this.arg$1 = newContactActivity;
    }

    public void didSelectCountry(String str, String str2) {
        this.arg$1.lambda$null$4$NewContactActivity(str, str2);
    }
}
