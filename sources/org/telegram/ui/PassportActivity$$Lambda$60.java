package org.telegram.ui;

import android.view.View;
import org.telegram.ui.CountrySelectActivity.CountrySelectActivityDelegate;

final /* synthetic */ class PassportActivity$$Lambda$60 implements CountrySelectActivityDelegate {
    private final PassportActivity arg$1;
    private final View arg$2;

    PassportActivity$$Lambda$60(PassportActivity passportActivity, View view) {
        this.arg$1 = passportActivity;
        this.arg$2 = view;
    }

    public void didSelectCountry(String str, String str2) {
        this.arg$1.lambda$null$46$PassportActivity(this.arg$2, str, str2);
    }
}
