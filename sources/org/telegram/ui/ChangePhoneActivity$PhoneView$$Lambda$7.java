package org.telegram.ui;

import org.telegram.ui.ChangePhoneActivity.PhoneView;
import org.telegram.ui.CountrySelectActivity.CountrySelectActivityDelegate;

final /* synthetic */ class ChangePhoneActivity$PhoneView$$Lambda$7 implements CountrySelectActivityDelegate {
    private final PhoneView arg$1;

    ChangePhoneActivity$PhoneView$$Lambda$7(PhoneView phoneView) {
        this.arg$1 = phoneView;
    }

    public void didSelectCountry(String str, String str2) {
        this.arg$1.lambda$null$1$ChangePhoneActivity$PhoneView(str, str2);
    }
}
