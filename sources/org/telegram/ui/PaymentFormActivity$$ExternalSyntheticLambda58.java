package org.telegram.ui;

import org.telegram.ui.CountrySelectActivity;

public final /* synthetic */ class PaymentFormActivity$$ExternalSyntheticLambda58 implements CountrySelectActivity.CountrySelectActivityDelegate {
    public final /* synthetic */ PaymentFormActivity f$0;

    public /* synthetic */ PaymentFormActivity$$ExternalSyntheticLambda58(PaymentFormActivity paymentFormActivity) {
        this.f$0 = paymentFormActivity;
    }

    public final void didSelectCountry(CountrySelectActivity.Country country) {
        this.f$0.lambda$createView$5(country);
    }
}