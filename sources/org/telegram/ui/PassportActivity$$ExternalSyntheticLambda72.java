package org.telegram.ui;

import android.view.View;
import org.telegram.ui.CountrySelectActivity;

public final /* synthetic */ class PassportActivity$$ExternalSyntheticLambda72 implements CountrySelectActivity.CountrySelectActivityDelegate {
    public final /* synthetic */ PassportActivity f$0;
    public final /* synthetic */ View f$1;

    public /* synthetic */ PassportActivity$$ExternalSyntheticLambda72(PassportActivity passportActivity, View view) {
        this.f$0 = passportActivity;
        this.f$1 = view;
    }

    public final void didSelectCountry(String str, String str2) {
        this.f$0.lambda$createIdentityInterface$45(this.f$1, str, str2);
    }
}
