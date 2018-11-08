package org.telegram.p005ui;

import org.telegram.p005ui.CountrySelectActivity.CountrySelectActivityDelegate;

/* renamed from: org.telegram.ui.PassportActivity$$Lambda$62 */
final /* synthetic */ class PassportActivity$$Lambda$62 implements CountrySelectActivityDelegate {
    private final PassportActivity arg$1;

    PassportActivity$$Lambda$62(PassportActivity passportActivity) {
        this.arg$1 = passportActivity;
    }

    public void didSelectCountry(String str, String str2) {
        this.arg$1.lambda$null$28$PassportActivity(str, str2);
    }
}
