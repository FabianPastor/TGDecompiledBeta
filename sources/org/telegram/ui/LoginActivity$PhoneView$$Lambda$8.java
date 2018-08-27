package org.telegram.ui;

import org.telegram.ui.CountrySelectActivity.CountrySelectActivityDelegate;
import org.telegram.ui.LoginActivity.PhoneView;

final /* synthetic */ class LoginActivity$PhoneView$$Lambda$8 implements CountrySelectActivityDelegate {
    private final PhoneView arg$1;

    LoginActivity$PhoneView$$Lambda$8(PhoneView phoneView) {
        this.arg$1 = phoneView;
    }

    public void didSelectCountry(String str, String str2) {
        this.arg$1.lambda$null$1$LoginActivity$PhoneView(str, str2);
    }
}
