package org.telegram.p005ui;

import org.telegram.p005ui.CountrySelectActivity.CountrySelectActivityDelegate;
import org.telegram.p005ui.LoginActivity.PhoneView;

/* renamed from: org.telegram.ui.LoginActivity$PhoneView$$Lambda$9 */
final /* synthetic */ class LoginActivity$PhoneView$$Lambda$9 implements CountrySelectActivityDelegate {
    private final PhoneView arg$1;

    LoginActivity$PhoneView$$Lambda$9(PhoneView phoneView) {
        this.arg$1 = phoneView;
    }

    public void didSelectCountry(String str, String str2) {
        this.arg$1.lambda$null$1$LoginActivity$PhoneView(str, str2);
    }
}
