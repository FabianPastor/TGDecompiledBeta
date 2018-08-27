package org.telegram.ui;

import org.telegram.ui.CountrySelectActivity.CountrySelectActivityDelegate;

final /* synthetic */ class PaymentFormActivity$$Lambda$41 implements CountrySelectActivityDelegate {
    private final PaymentFormActivity arg$1;

    PaymentFormActivity$$Lambda$41(PaymentFormActivity paymentFormActivity) {
        this.arg$1 = paymentFormActivity;
    }

    public void didSelectCountry(String str, String str2) {
        this.arg$1.lambda$null$5$PaymentFormActivity(str, str2);
    }
}
